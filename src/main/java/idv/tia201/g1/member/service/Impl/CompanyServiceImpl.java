package idv.tia201.g1.member.service.Impl;
import idv.tia201.g1.chat.service.ChatService;
import idv.tia201.g1.member.dto.*;
import idv.tia201.g1.member.dao.CompanyDao;
import idv.tia201.g1.member.entity.User;
import idv.tia201.g1.member.service.CompanyService;
import idv.tia201.g1.member.entity.Company;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


import java.util.Date;
import java.util.List;

@CommonsLog
@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyDao companyDao;
    @Autowired
    private ChatService chatService;

    @Override
    public Company login(CompanyLoginRequest companyLoginRequest) {
        Company company = companyDao.findByUsername(companyLoginRequest.getUsername());
       //檢查帳號
        if (company == null) {
            throw new IllegalArgumentException("帳號或密碼有誤");
        }
        //使用MD5生成加密
        String hashedPassword = DigestUtils.md5DigestAsHex(companyLoginRequest.getPassword().getBytes());

        //檢查密碼
        if (!hashedPassword.equals(company.getPassword())) {
            throw new IllegalArgumentException("帳號或密碼有誤");
        }
        return company;
    }

    @Override
    public Company register(CompanyRegisterRequest companyRegisterRequest) {
        //// 檢查註冊
        Company existingCompany = companyDao.findByUsernameOrVatNumberOrCompanyName(
                companyRegisterRequest.getUsername(),
                companyRegisterRequest.getVatNumber(),
                companyRegisterRequest.getCompanyName()
        );
        if (existingCompany != null) {
            throw new IllegalArgumentException("存在已註冊過資訊");
        }
            Company newcompany = new Company();
//            newcompany.setUsername(companyRegisterRequest.getUsername());
//            newcompany.setCompanyName(companyRegisterRequest.getCompanyName());
//            newcompany.setPassword(companyRegisterRequest.getPassword());
//            newcompany.setVatNumber(companyRegisterRequest.getVatNumber());
//            newcompany.setAddress(companyRegisterRequest.getAddress());
//            newcompany.setTelephone(companyRegisterRequest.getTelephone());
            BeanUtils.copyProperties(companyRegisterRequest, newcompany);
            //使用MD5生成加密
            String hashedPassword = DigestUtils.md5DigestAsHex(companyRegisterRequest.getPassword().getBytes());
            newcompany.setPassword(hashedPassword);
            // 創建帳號
            companyDao.save(newcompany);


            chatService.initChatRoomWithAdmin(newcompany);
            return newcompany;

    }

    @Override
    public Company findByCompanyId(Integer companyId) {
        return companyDao.findByCompanyId(companyId);
    }



    @Override
    public Company UpdateCompany(Integer companyId, CompanyUpdateRequest companyUpdateRequest) {
        Company company = companyDao.findByCompanyId(companyId);

        if (company == null) {
            throw new IllegalArgumentException("不存在此商家");
        }

        if ( companyUpdateRequest.getUsername()!=null && !companyUpdateRequest.getUsername().equals(company.getUsername())) {
            company.setUsername(companyUpdateRequest.getUsername());
        }

//        if (companyUpdateRequest.getPassword() !=null && !companyUpdateRequest.getPassword().equals(company.getPassword())) {
//             String hashedPassword = DigestUtils.md5DigestAsHex(company.getPassword().getBytes());
//            company.setPassword(hashedPassword);
//        }

        if (companyUpdateRequest.getCompanyName()!=null && !companyUpdateRequest.getCompanyName().equals(company.getCompanyName())) {
            company.setCompanyName(companyUpdateRequest.getCompanyName());
        }

        if (companyUpdateRequest.getVatNumber()!=null && !companyUpdateRequest.getVatNumber().equals(company.getVatNumber())) {
            company.setVatNumber(companyUpdateRequest.getVatNumber());
        }

        if (companyUpdateRequest.getAddress()!=null && !companyUpdateRequest.getAddress().equals(company.getAddress())) {
            company.setAddress(companyUpdateRequest.getAddress());
        }

        if (companyUpdateRequest.getTelephone()!=null && !companyUpdateRequest.getTelephone().equals(company.getTelephone())) {
            company.setTelephone(companyUpdateRequest.getTelephone());
        }

//        if (companyUpdateRequest.getChangeId()!=null && !companyUpdateRequest.getChangeId().equals(company.getChangeId())) {
//            company.setChangeId(companyUpdateRequest.getChangeId());
//        }

        company.setLastModifiedDate(new Date());

        Company companysaved = companyDao.save(company);
        return companysaved;
    }

    @Override
    public Company findByUsernameOrVatNumberOrCompanyName(String username, String vatNumber, String companyName) {
        return companyDao.findByUsernameOrVatNumberOrCompanyName(username, vatNumber, companyName);
    }

    @Override
    public Integer countCompany() {
        return companyDao.countCompany();
    }

    @Override
    public Page<Company> findAll(CompanyQueryParams companyQueryParams) {
        // 取得 page、size 和 sort 參數
        int page = Math.max(0, companyQueryParams.getOffset()); // 確保 page 不小於 0
        int size = Math.min(Math.max(0, companyQueryParams.getLimit()), 1000); // 限制 size 的值在 0 到 1000 之間
        String sort = companyQueryParams.getSort(); // 取得排序方式

        // 建立排序規則
        Sort.Direction sortDirection = Sort.Direction.fromString(sort);
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, "createdDate") // 以 createdDate 進行排序
        );

        // 傳遞 pageable 給 findAll 方法
        return companyDao.findAll(pageable);
    }

    @Override
    public Company deleteCompany(Integer companyId) {
        return companyDao.findByCompanyId(companyId);
    }

    @Override
    public List<Company> findCompaniesList(CompanyQueryParams companyQueryParams) {
        int offset = companyQueryParams.getOffset();
        int limit = companyQueryParams.getLimit();
        int page = offset / limit ;
        int pageOffest = offset % limit ;

        Sort sort = null;
        if("desc".equals(companyQueryParams.getSort())) {
            sort = sort.by(companyQueryParams.getOrderBy()).descending();
        }else {
            sort = sort.by(companyQueryParams.getOrderBy()).ascending();
        }

        Pageable pageable = PageRequest.of(
                page,
                limit,
                sort
        );

        List<Company> companies = companyDao.findAll(pageable).getContent();

        if(pageOffest >0){
            companies =companies.subList(pageOffest, Math.min(companies.size(), limit));
        }
        return companies;
    }

    @Override
    public Long count() {

        return companyDao.count();

    }
}
