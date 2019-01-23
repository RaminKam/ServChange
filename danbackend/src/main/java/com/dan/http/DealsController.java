package com.dan.http;

import com.dan.http.payload.ReviewPayload;
import com.dan.http.payload.common.PagedResponse;
import com.dan.http.payload.deals.ConflictResponse;
import com.dan.http.payload.deals.DealInfoPayload;
import com.dan.http.payload.deals.DealRequest;
import com.dan.http.payload.deals.DealResponse;
import com.dan.http.payload.services.ServicesQueryParams;
import com.dan.security.CurrentUser;
import com.dan.security.UserPrincipal;
import com.dan.service.DealsService;
import com.dan.service.ReviewService;
import com.dan.service.UserService;
import com.dan.util.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.dan.http.common.Responses.success;

@RestController
@RequestMapping("/api/deals")
public class DealsController {


    @Autowired
    private DealsService dealsService;

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserService userService;

    @PostMapping("/newdeal")
    @PreAuthorize("hasRole('USER')")
    public DealResponse newDeal(@RequestBody DealRequest dealRequest, @CurrentUser UserPrincipal currentUser) {

        return dealsService.newDeal(dealRequest, currentUser);
    }


    @PostMapping("/{dealId}")
    @PreAuthorize("hasAnyRole('USER','CURATOR','MAIN_CURATOR','MAIN_ADMIN','ADMIN')")
    public DealInfoPayload dealById(
            @PathVariable Long dealId, @CurrentUser UserPrincipal currentUser) {



        return dealsService.showDeal(dealId, currentUser);
    }

    @PostMapping("/action/")
    @PreAuthorize("hasAnyRole('USER','CURATOR','MAIN_CURATOR')")
    public DealResponse cancelDeal(
            @RequestBody DealRequest dealRequest, @CurrentUser UserPrincipal currentUser) {

        return dealsService.changeStatus(dealRequest, currentUser);
    }


    @PostMapping("/newReview")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity writeReview(
            @RequestBody ReviewPayload reviewPayload, @CurrentUser UserPrincipal currentUser) {

        reviewService.newReview(reviewPayload, currentUser);

        return success("success_review");
    }
    @PostMapping("/sendCuratorReview")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity sendCuratorReview(
            @RequestBody ReviewPayload reviewPayload, @CurrentUser UserPrincipal currentUser) {

        reviewService.newCuratorReview(reviewPayload, currentUser);

        return success("success_review");
    }
    @PostMapping("/list")
    public PagedResponse<DealInfoPayload> getMyDeals(@CurrentUser UserPrincipal currentUser,
                                                     @RequestBody() ServicesQueryParams params,
                                                     @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
                                                     @RequestParam(value = "sortDirection", defaultValue = "DESC") String direction,
                                                     @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                     @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        return dealsService.getMyDeals(currentUser, params, page, size, sort, Sort.Direction.valueOf(direction));
    }

    @PostMapping("/adminlist")
    @PreAuthorize("hasAnyRole('CURATOR','MAIN_CURATOR','MAIN_ADMIN','ADMIN')")
    public PagedResponse<ConflictResponse> adminList(@CurrentUser UserPrincipal currentUser,
                                                     @RequestBody() ServicesQueryParams params,
                                                     @RequestParam(value = "sort", defaultValue = "createdAt") String sort,
                                                     @RequestParam(value = "sortDirection", defaultValue = "DESC") String direction,
                                                     @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                     @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {

        return dealsService.getCuratorDeals(currentUser, params, page, size, sort, Sort.Direction.valueOf(direction));
    }


}
