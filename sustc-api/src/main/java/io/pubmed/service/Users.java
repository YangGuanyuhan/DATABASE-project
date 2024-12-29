package io.pubmed.service;

public interface Users {
    boolean insertAccount(int userId, String usename,String password);

    // 查询账号信息
    int findFirstDigitOfUserId(int userId, String usename,String password);
}
