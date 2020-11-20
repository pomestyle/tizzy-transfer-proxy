package com.udeam.edu.service.impl;

import com.udeam.edu.dao.AccountDao;
import com.udeam.edu.dao.impl.JdbcAccountDaoImpl;
import com.udeam.edu.factory.BeanFactorys;
import com.udeam.edu.pojo.Account;
import com.udeam.edu.service.TransferService;

/**
 * @author 应癫
 */
public class TransferServiceImpl implements TransferService {

    // 1 原始的new 方法创建dao接口实现类对象
    //private AccountDao accountDao = new JdbcAccountDaoImpl();

    //2 从ioc bean工厂获取
    //private AccountDao accountDao = (AccountDao) BeanFactorys.getBean("accountDao");

    // 3 从ioc bean工厂获取,并且set 赋值注入
    private AccountDao accountDao;

    public void setAccountDao(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Override
    public void transfer(String fromCardNo, String toCardNo, int money) throws Exception {

        Account from = accountDao.queryAccountByCardNo(fromCardNo);
            Account to = accountDao.queryAccountByCardNo(toCardNo);

            from.setMoney(from.getMoney()-money);
            to.setMoney(to.getMoney()+money);

            accountDao.updateAccountByCardNo(to);
            //int c = 1/0;
            accountDao.updateAccountByCardNo(from);

    }
}
