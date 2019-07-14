package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UsersEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UsersEntity createUser(UsersEntity usersEntity) throws SignUpRestrictedException {
        if(userDao.getUserByUsername(usersEntity.getUserName())!=null){
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }else if(userDao.getUserByEmail(usersEntity.getEmail())!= null){
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }else{
            String[] encryptedPassword = cryptographyProvider.encrypt(usersEntity.getPassword());
            usersEntity.setSalt(encryptedPassword[0]);
            usersEntity.setPassword(encryptedPassword[1]);
            userDao.createUser(usersEntity);
            return usersEntity;
        }
    }
}