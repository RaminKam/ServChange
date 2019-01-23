package com.dan.service;

import com.dan.db.entities.auth.RoleName;
import com.dan.db.entities.auth.UserDB;
import com.dan.db.entities.auth.UserStatus;
import com.dan.db.entities.common.UniverDB;
import com.dan.db.entities.services.*;
import com.dan.db.repository.ConflictRepository;
import com.dan.db.repository.DealHistoryRepository;
import com.dan.db.repository.DealsRepository;
import com.dan.db.repository.MCConflictRepository;
import com.dan.exception.AppError;
import com.dan.exception.AppException;
import com.dan.http.payload.common.PagedResponse;
import com.dan.http.payload.deals.ConflictResponse;
import com.dan.http.payload.deals.DealInfoPayload;
import com.dan.http.payload.deals.DealRequest;
import com.dan.http.payload.deals.DealResponse;
import com.dan.http.payload.services.ServicesQueryParams;
import com.dan.security.UserPrincipal;
import com.dan.service.audit.AuditService;
import com.dan.service.audit.AuditType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.dan.db.entities.services.DealStatus.*;
import static com.dan.exception.AppError.USER_NOT_MODERATED;
import static com.dan.http.common.Responses.ifNotFoundShowError;
import static java.util.Arrays.asList;


@Service
public class DealsService {

	@Autowired
    NotificationService notifications;

    @Autowired
    private DealsRepository dealsRepository;

    @Autowired
    private DealHistoryRepository history;

    @Autowired
    private AuditService audit;

    @Autowired
    private ServicesService servicesService;
    @Autowired
    private UserService userService;

    @Autowired
    private FinancesServices financesServices;

    @Autowired
    private MessagesService messagesService;

    private static final Logger logger = LoggerFactory.getLogger(DealsService.class);


    public DealResponse newDeal(DealRequest dealRequest, UserPrincipal user) {
        DealDB deal = new DealDB();
        UserDB buyer = userService.byId(user);
        if (!buyer.getStatus().equals(UserStatus.APPROVED)) {
            AppException.error(USER_NOT_MODERATED);
        }


        ServiceDB service = servicesService.byId(dealRequest.getService());
        if (buyer.getId().equals(service.getUser().getId()))
            AppException.error(AppError.NEW_DEAL_NOT_ALLOWED);
        deal.setCost(service.getCost());
        financesServices.checkBalance(buyer, deal.getDealCost());
        deal.setService(service);
        deal.setBuyer(buyer);

/*        deal.setContract(dealRequest.getContract());
        deal.setStartDeal(dealRequest.getStartDeal());
        deal.setEndDeal(dealRequest.getEndDeal());*/
        deal.setStatus(WAITING_DEAL_RESULT);//WAITING_BUYER //Feature #188


        if (dealRequest.getT1() != null && dealRequest.getT1() > 0) {
            LocalDateTime today = LocalDateTime.now();
            deal.setAutoCancel(today.plusHours(dealRequest.getT1()).toInstant(OffsetDateTime.now().getOffset()));
        }


        dealsRepository.save(deal);
        financesServices.blockCost(deal);//Feature #188


        audit.audit(AuditType.CREATE_NEW_DEAL, deal, deal.getStatus(), buyer);
		
		
		
		notifications.sendToAdminNotif(service.getUser().getEmail(), 
		"Deeds & Needs - Notification", 
		"Hi, it's support from Deeds & Needs, you have deeds for needs from: "+buyer.getName()+"."
				+ "Needer is waiting for your answer.");
		
		
        return new DealResponse(deal);
    }

    private void history(DealDB deal, InitiatorKind initiator, UserDB user) {
        DealHistoryDB historyDB = new DealHistoryDB();
        historyDB.setInitiator(initiator);
        historyDB.setStatus(deal.getStatus());
        historyDB.setDeal(deal);
        historyDB.setContract(deal.getContract());
        historyDB.setUser(user);
        history.save(historyDB);
    }

    public InitiatorKind initiator(DealDB deal, UserDB currentUser) {
        return deal.checkPermissionsAndGetInitiator(currentUser);
    }

    @Autowired
    SocketMessagesService socketMessagesService;


    public DealResponse changeStatus(DealRequest dealRequest, UserPrincipal user) {

        DealStatus newStatus = dealRequest.getAction();
        UserDB currentUser = userService.byId(user);

        DealDB deal = getDealById(dealRequest.getId(), user);

        if (newStatus.equals(deal.getStatus()))
            AppException.error(AppError.CANNOT_SWITCH_STATUS);

        if (newStatus.equals(DealStatus.RULES_GENERATED)) {
            deal.setContract(dealRequest.getContract());
            deal.setStartDeal(dealRequest.getStartDeal());
            deal.setEndDeal(dealRequest.getEndDeal());
        }
        InitiatorKind initiator = initiator(deal, currentUser);

        if (!RULES.get(deal.getStatus()).contains(newStatus))
            AppException.error(AppError.CANNOT_SWITCH_STATUS);

        if (!CAN_SET.get(initiator).contains(newStatus))
            AppException.error(AppError.CANNOT_USER_INITIATOR);

        checkConflict(newStatus, dealRequest.getComments(), currentUser, deal);

        if (newStatus.equals(WAITING_DEAL_RESULT))
            financesServices.blockCost(deal);


        if (newStatus.equals(CURATOR_APPROVE_DEAL) || newStatus.equals(CURATOR_REJECT_DEAL)) {
            if (currentUser.getUserRole().equals(RoleName.ROLE_MAIN_CURATOR)) {
                if (newStatus.equals(CURATOR_APPROVE_DEAL))
                    deal.getMcconflict().setStatus(ConflictStatus.APPROVE_DEAL);
                if (newStatus.equals(CURATOR_REJECT_DEAL))
                    deal.getMcconflict().setStatus(ConflictStatus.REJECT_DEAL);
                newStatus = deal.getMcconflict().getDealStatus();
            } else {
                deal.getConflict().setAutoCancelIn(Instant.now().plusSeconds(3600 * 24));
                if (newStatus.equals(CURATOR_APPROVE_DEAL))
                    deal.getConflict().setStatus(ConflictStatus.APPROVE_DEAL);
                if (newStatus.equals(CURATOR_REJECT_DEAL))
                    deal.getConflict().setStatus(ConflictStatus.REJECT_DEAL);

            }

        }
        if (newStatus.equals(MAIN_CURATOR_REVIEW)) {
            deal.getConflict().setAutoCancelIn(null);
            conflictToMainCurator(dealRequest, currentUser);
            conflictRepository.save(deal.getConflict());
        }
        if (newStatus.equals(APPROVE_CURATOR_SOLUTION)) {
            DealConflictDB conflict = deal.getConflict();
            switch (initiator) {
                case SELLER:
                    conflict.setSellerApproveResult(true);
                    break;
                case BUYER:
                    conflict.setBuyerApproveResult(true);
                    break;

            }

            if (conflict.isSolved()) {
                logger.info("Conflict is solved");
                conflict.setAutoCancelIn(null);
                newStatus = conflict.getDealStatus();
                finishDeal(deal, conflict.getDealStatus());
            }
            conflictRepository.save(conflict);
        } else {
            finishDeal(deal, newStatus);
            deal.setStatus(newStatus);
        }


        logger.info("Save deal status:" + deal.getStatus());
        dealsRepository.save(deal);
        socketMessagesService.dealUpdate(deal.getId());
        audit.audit(AuditType.DEAL_UPDATE_STATUS, deal, deal.getStatus(), currentUser);

        history(deal, initiator, currentUser);

        if (newStatus.equals(DealStatus.CURATOR_REVIEW))
            conflict(deal, dealRequest.getComments(), currentUser);


        return new DealResponse(deal);
    }

    private void finishDeal(DealDB deal, DealStatus newStatus) {
        logger.info("finishDeal:" + deal.getStatus() + "->" + newStatus);
        if (!FINISHED_STATUSES.contains(deal.getStatus())) {

            if (newStatus.equals(FINISHED))
                financesServices.transferDeal(deal);


            if (newStatus.equals(CLOSED) && DealStatus.RESULT_WAITING.contains(deal.getStatus()))
                financesServices.resetBlock(deal);
            deal.setStatus(newStatus);

        }
    }

    private void checkConflict(DealStatus newStatus, String comments, UserDB currentUser, DealDB deal) {
        if (deal.getStatus().equals(DealStatus.CURATOR_REVIEW) && currentUser.getUserRole().equals(RoleName.ROLE_CURATOR)) {
            if (deal.getConflict() != null) {
                DealConflictDB conflict = deal.getConflict();
                if (newStatus.equals(FINISHED)) {
                    conflict.setStatus(ConflictStatus.REJECT_DEAL);
                }
                if (newStatus.equals(CLOSED)) {
                    conflict.setStatus(ConflictStatus.APPROVE_DEAL);
                }
                conflict.setCuratorComments(comments);
                conflictRepository.save(conflict);
            }

        }
    }


    private DealDB getDealById(Long dealId) {
        return ifNotFoundShowError(dealsRepository.findById(dealId), AppError.DEAL_NOT_FOUND);
    }

    void autoCloseDeals() {

        conflictRepository.findByAutoCancelInBefore(Instant.now()).forEach(conflict -> {
            conflict.setAutoCancelIn(null);
            conflictRepository.save(conflict);
            DealDB deal = conflict.getDeal();
            finishDeal(deal, conflict.getDealStatus());
            dealsRepository.save(deal);
        });

        dealsRepository.findInAndAutoCancelNotNullAndAutoCancelBefore(Instant.now()).forEach(deal -> {
            deal.setStatus(CLOSED);
            dealsRepository.save(deal);
            audit.audit(deal, null);
            history(deal, InitiatorKind.SYSTEM, null);
        });

        dealsRepository.findInAndAutoModeratingNotNullAndAutoModeratingBefore(Instant.now()).forEach(deal -> {
            deal.setStatus(DealStatus.CURATOR_REVIEW);
            dealsRepository.save(deal);
            audit.audit(deal, null);
            history(deal, InitiatorKind.SYSTEM, null);
        });

    }

    public DealDB getDealById(Long dealId, UserPrincipal currentUser) {
        DealDB deal = getDealById(dealId);
        UserDB user = userService.byId(currentUser);
        deal.checkPermissionsAndGetInitiator(user);


        return deal;
    }

    public DealInfoPayload showDeal(Long dealId, UserPrincipal currentUser) {

        DealDB deal = getDealById(dealId, currentUser);
        messagesService.markSeen(deal, currentUser);
        return new DealInfoPayload(deal, initiator(deal, userService.byId(currentUser)));
    }

    public PagedResponse<ConflictResponse> getCuratorDeals(UserPrincipal currentUser, ServicesQueryParams params, int page, int size, String sort, Sort.Direction direction) {

        CommonService.validatePageNumberAndSize(page, size);

        UserDB user = userService.byId(currentUser);

        UniverDB univer = user.getUniver();

        Pageable pageable = PageRequest.of(page, size, direction, sort);


        Page<? extends ConflictDB> deals = null;
        if (currentUser.check(RoleName.ROLE_ADMIN) || currentUser.check(RoleName.ROLE_MAIN_ADMIN)) {

            UserDB targetUser = userService.byId(params.getUserId());

            if (targetUser.getUserRole().equals(RoleName.ROLE_CURATOR))
                deals = conflictRepository.findConflictsForAdmin(targetUser,
                        params.getFilter().getConflictStatus(), pageable);
            else if (targetUser.getUserRole().equals(RoleName.ROLE_MAIN_CURATOR))
                deals = mcConflictRepository.findConflictsForAdmin(targetUser,
                        params.getFilter().getConflictStatus(), pageable);


        } else
            deals = currentUser.check(RoleName.ROLE_MAIN_CURATOR) ?
                    (params.getFilter().getActive() != null && params.getFilter().getActive() ?
                            mcConflictRepository.findForMainCurator(params.getFilter().getConflictStatus(), params.getFilter().getCuratorId(), univer, pageable) :
                            conflictRepository.findForMainCurator(params.getFilter().getConflictStatus(), params.getFilter().getCuratorId(),
                                    univer, params.getFilter().getUsedmaincurator() == null || !params.getFilter().getUsedmaincurator() ? null : true,
                                    pageable)) :
                    conflictRepository.findForCurator(user, params.getFilter().getConflictStatus(), univer, pageable);


        return new PagedResponse<>((deals.getNumberOfElements() == 0) ? Collections.emptyList() : deals.getContent().stream().map(ConflictResponse::new).collect(Collectors.toList()), deals.getNumber(),
                deals.getSize(), deals.getTotalElements(), deals.getTotalPages(), deals.isLast());


    }


    public void resetConflictsForCurator(UserDB curator) {
        conflictRepository.findForCurator(curator).forEach(c -> {
            c.setCurator(userService.randomCurator(c.getUser().getUniver()));
            conflictRepository.save(c);
        });

    }
    public void resetMCConflictsForCurator(UserDB curator) {
        mcConflictRepository.findForMainCurator(curator).forEach(c -> {
            c.setCurator(userService.randomCurator(c.getUser().getUniver()));
            mcConflictRepository.save(c);
        });

    }
    public PagedResponse<DealInfoPayload> getMyDeals(UserPrincipal currentUser, ServicesQueryParams params, int page, int size, String sort, Sort.Direction direction) {

        CommonService.validatePageNumberAndSize(page, size);

        UserDB user = userService.byId(currentUser);


        Pageable pageable = PageRequest.of(page, size, direction, sort);
        Page<DealDB> deals = Page.empty();


        deals = dealsRepository.findByUserAndStatusIn(user,
                params.getFilter().getDealStatuses().isEmpty() ? DealStatus.ALL :
                        params.getFilter().getDealStatuses().stream().map(i -> DealStatus.values()[i]).collect(Collectors.toList()), params.getSearch(),

                params.getFilterSellerBuyer().isEmpty() ? asList(0, 1) : params.getFilterSellerBuyer(), pageable);


        return new PagedResponse<>((deals.getNumberOfElements() == 0) ? Collections.emptyList() : deals.getContent().stream().map(DealInfoPayload::new).collect(Collectors.toList()), deals.getNumber(),
                deals.getSize(), deals.getTotalElements(), deals.getTotalPages(), deals.isLast());


    }

    @Autowired
    ConflictRepository conflictRepository;


    @Autowired
    MCConflictRepository mcConflictRepository;

    private void conflict(DealDB deal, String comments, UserDB user) {
        if (deal.getConflict() == null) {
            DealConflictDB conflict = new DealConflictDB();
            conflict.setDeal(deal);
            conflict.setUser(user);
            conflict.setComments(comments);
            UserDB curator = findCurator(deal);
            if (curator == null) {
                AppException.error(AppError.NO_CURATOR_FOR_THIS_UNIVER);
            }
            conflict.setCurator(curator);
            conflict.setStatus(ConflictStatus.INITIAL);
            conflictRepository.save(conflict);
        }

    }

    public void conflictToMainCurator(DealRequest dealRequest, UserDB user) {
        DealDB deal = getDealById(dealRequest.getId());


        if (deal.getMcconflict() == null && (deal.getConflict() != null)) {

            InitiatorKind initiator = initiator(deal, user);
            DealMainCuratorConflictDB conflict = new DealMainCuratorConflictDB();
            conflict.setDeal(deal);
            conflict.setCurator(findMainCurator(deal));
            conflict.setStatus(ConflictStatus.INITIAL);
            conflict.setComments(dealRequest.getComments());
            conflict.setUser(user);
            mcConflictRepository.save(conflict);
            DealHistoryDB historyDB = new DealHistoryDB();
            historyDB.setInitiator(initiator);
            historyDB.setStatus(MAIN_CURATOR_REVIEW);
            historyDB.setDeal(deal);
            historyDB.setContract(deal.getContract());
            historyDB.setUser(user);
            history.save(historyDB);
        }
    }


    public UserDB findCurator(DealDB deal) {
        return userService.randomCurator(deal.getService().getUser().getUniver());
    }

    public UserDB findMainCurator(DealDB deal) {
        return userService.randomMainCurator(deal.getService().getUser().getUniver());
    }

    @Scheduled(fixedRate = 5000)
    public void closeDeals() {
        autoCloseDeals();

    }

    @Autowired
    UniverService univerService;

    public PagedResponse<DealInfoPayload> getDeals(ServicesQueryParams params, int page, int size, String sort, Sort.Direction direction) {
        CommonService.validatePageNumberAndSize(page, size);


        Pageable pageable = PageRequest.of(page, size, direction, sort);
        Page<DealDB> deals = Page.empty();


        deals = dealsRepository.allDeals(
                (params.getFilter().getDealStatuses().isEmpty() ? DealStatus.ALL :
                        params.getFilter().getDealStatuses().stream().map(i -> DealStatus.values()[i]).collect(Collectors.toList())),

                univerService.byId(params.getFilter().getUniverId()),
                (params.getUserId() != null ? userService.byId(params.getUserId()) : null),
                pageable);


        return new PagedResponse<>((deals.getNumberOfElements() == 0) ? Collections.emptyList() : deals.getContent().stream().map(DealInfoPayload::new).collect(Collectors.toList()), deals.getNumber(),
                deals.getSize(), deals.getTotalElements(), deals.getTotalPages(), deals.isLast());
    }
}

