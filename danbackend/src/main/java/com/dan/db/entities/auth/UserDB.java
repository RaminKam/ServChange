package com.dan.db.entities.auth;


import com.dan.db.entities.ResourceDB;
import com.dan.db.entities.audit.ModerableDB;
import com.dan.db.entities.common.UniverDB;
import com.dan.db.entities.moderate.ModerateResultDB;
import com.dan.db.entities.services.ServiceDB;
import com.dan.http.payload.user.ProfileRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {
                "username"
        }),
        @UniqueConstraint(columnNames = {
                "email"
        }),
        @UniqueConstraint(columnNames = {
                "studentid"
        })
})

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UserDB extends ModerableDB {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 90)
    private String name;
    @NotBlank
    @Size(max = 90)
    private String username;

    @NotBlank
    @Size(max = 90)
    private String secondname;


    @Size(max = 90)
    private String studentid;

    @NotBlank
    @Size(max = 4)
    private String lang;


    @NotBlank


    private String email;

    private String city;


    private String verifyCode;

    private Boolean verificated;

    private Boolean active;


    private String about;

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
    @ManyToOne
    @JoinColumn(name = "logo_id")
    private ResourceDB logoResource;

    public ResourceDB getLogoResource() {
        return logoResource;
    }

    public void setLogoResource(ResourceDB logoResource) {
        this.logoResource = logoResource;
    }

    @Lob
    @Column(columnDefinition = "longtext",name = "avatar")
    @Type(type = "org.hibernate.type.TextType")
    private String avatarLogo;

    public String getAvatarLogo() {
        return avatarLogo;
    }

    public void setAvatarLogo(String avatar) {
        this.avatarLogo = avatar;
    }
    @Transient
    public String getAvatar(){
        return logoResource!=null?"/api/resources/download/"+logoResource.getId():null;
    }

    @NotBlank
    @Size(max = 100)
    @JsonIgnore
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleDB> roles = new HashSet<>();
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_resources",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private Set<ResourceDB> resources = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "univer_id")

    private UniverDB univer;

    public UniverDB getUniver() {
        return univer;
    }

    public void setUniver(UniverDB univer) {
        this.univer = univer;
    }

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true
    )
    @Fetch(FetchMode.SELECT)
    @JsonIgnore
    private List<ServiceDB> services = new ArrayList<>();


    public UserDB() {

    }


    public UserDB(ProfileRequest signUpRequest) {
        super();
        name = signUpRequest.getName();
        email = signUpRequest.getEmail();
        secondname = signUpRequest.getSecondname();
        wechatid=signUpRequest.getWechatid();
        phone=signUpRequest.getPhone();
        username = signUpRequest.getUsername();
        password = signUpRequest.getPassword();
        lang = signUpRequest.getLang();
        studentid = signUpRequest.getStudentid();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @JsonIgnore
    public Set<RoleDB> getRoles() {
        return roles;
    }

    public void setRoles(Set<RoleDB> roles) {
        this.roles = roles;
    }

    public String getSecondname() {
        return secondname;
    }

    public void setSecondname(String secondname) {
        this.secondname = secondname;
    }

    public String getLang() {
        return lang;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }


    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public List<String> getRoleNames() {
        return getRoles().stream().map(p -> p.getName().toString()).collect(Collectors.toList());
    }

    public Set<ResourceDB> getResources() {
        return resources;
    }

    public void setResources(Set<ResourceDB> resources) {
        this.resources = resources;
    }

    @JsonIgnore
    public List<ServiceDB> getServices() {
        return services;
    }

    public void setServices(List<ServiceDB> services) {
        this.services = services;
    }


    @JsonIgnore
    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public Boolean getVerificated() {
        return verificated;
    }

    public void setVerificated(Boolean verificated) {
        this.verificated = verificated;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }


    public String resetCode() {
        String code = String.valueOf((int) (Math.random() * 100000));
        setVerifyCode(DigestUtils.md5Hex(code));
        setVerificated(false);
        return code;
    }


    private Long feedbackCount;
    private Float rate;
    private Long servicesCount;
    private Long dealsBuyCount;
    private Long dealsSellCount;



    public Long getFeedbackCount() {
        return feedbackCount;
    }

    public void setFeedbackCount(Long feedbackCount) {
        this.feedbackCount = feedbackCount;
    }

    public Float getRate() {
        return rate;
    }

    public void setRate(Float rate) {
        this.rate = rate;
    }

    public Long getServicesCount() {
        return servicesCount;
    }

    public void setServicesCount(Long servicesCount) {
        this.servicesCount = servicesCount;
    }

    public Long getDealsBuyCount() {
        return dealsBuyCount;
    }

    public void setDealsBuyCount(Long dealsBuyCount) {
        this.dealsBuyCount = dealsBuyCount;
    }

    private Long countDeals;


    public Long getCountDeals() {
        return countDeals;
    }

    public void setCountDeals(Long countDeals) {
        this.countDeals = countDeals;
    }

    public Long getDealsSellCount() {
        return dealsSellCount;
    }

    public void setDealsSellCount(Long dealsSellCount) {
        this.dealsSellCount = dealsSellCount;
    }


    private Long curatorClaims;
    private Long curatorAppoveClaims;
    private Long curatorRejectClaims;


    public Long getCuratorClaims() {
        return curatorClaims;
    }

    public void setCuratorClaims(Long curatorClaims) {
        this.curatorClaims = curatorClaims;
    }

    private Long claimToCuratorWork;

    public Long getClaimToCuratorWork() {
        return claimToCuratorWork;
    }

    public void setClaimToCuratorWork(Long claimToCuratorWork) {
        this.claimToCuratorWork = claimToCuratorWork;
    }

    public Long getCuratorAppoveClaims() {
        return curatorAppoveClaims;
    }

    public void setCuratorAppoveClaims(Long curatorAppoveClaims) {
        this.curatorAppoveClaims = curatorAppoveClaims;
    }

    public Long getCuratorRejectClaims() {
        return curatorRejectClaims;
    }

    public void setCuratorRejectClaims(Long curatorRejectClaims) {
        this.curatorRejectClaims = curatorRejectClaims;
    }

    private Long mainCuratorClaims;
    private Long mainProcessedCuratorClaims;

    public Long getMainCuratorClaims() {
        return mainCuratorClaims;
    }

    public void setMainCuratorClaims(Long mainCuratorClaims) {
        this.mainCuratorClaims = mainCuratorClaims;
    }

    public Long getMainProcessedCuratorClaims() {
        return mainProcessedCuratorClaims;
    }

    public void setMainProcessedCuratorClaims(Long mainProcessedCuratorClaims) {
        this.mainProcessedCuratorClaims = mainProcessedCuratorClaims;
    }

    private Long moderatorProcessedServices;
    private Long moderatorTranslatedServices;
    private Long moderatorApproveRegistrations;
    private Long moderatorRejectRegistrations;

    public Long getModeratorTranslatedServices() {
        return moderatorTranslatedServices;
    }

    public void setModeratorTranslatedServices(Long moderatorTranslatedServices) {
        this.moderatorTranslatedServices = moderatorTranslatedServices;
    }

    public Long getModeratorApproveRegistrations() {
        return moderatorApproveRegistrations;
    }

    public void setModeratorApproveRegistrations(Long moderatorApproveRegistrations) {
        this.moderatorApproveRegistrations = moderatorApproveRegistrations;
    }

    public Long getModeratorRejectRegistrations() {
        return moderatorRejectRegistrations;
    }

    public void setModeratorRejectRegistrations(Long moderatorRejectRegistrations) {
        this.moderatorRejectRegistrations = moderatorRejectRegistrations;
    }

    public RoleName getUserRole() {
        return getRoles().stream().map(RoleDB::getName).findFirst().orElse(RoleName.ROLE_USER);
    }

    public Long getModeratorProcessedServices() {
        return moderatorProcessedServices;
    }

    public void setModeratorProcessedServices(Long moderatorProcessedServices) {
        this.moderatorProcessedServices = moderatorProcessedServices;
    }

    private UserStatus status;

    @Enumerated(EnumType.STRING)
    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void updateStatus(ModerateResultDB res) {
        super.updateStatus(res);
        res.setUser(this);
        switch (res.getModerate()) {
            case APPROVED:
                status = UserStatus.APPROVED;
                break;
            case READY:
                status = UserStatus.READY;
                break;
            case REJECTED:
                status = UserStatus.REJECTED;
                break;
        }


    }



    private String verificationUrl;

    public String getVerificationUrl() {
        return verificationUrl;
    }

    public void setVerificationUrl(String verificationUrl) {
        this.verificationUrl = verificationUrl;
    }


    private Instant wasAt;


    public Instant getWasAt() {
        return wasAt;
    }

    public void setWasAt(Instant wasAt) {
        this.wasAt = wasAt;
    }

    private String wechatid;

    public String getWechatid() {
        return wechatid;
    }

    public void setWechatid(String wechatid) {
        this.wechatid = wechatid;
    }

    private boolean removed=false;

    @Column(nullable = false,columnDefinition="tinyint(1) default 1")
    public boolean getRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}