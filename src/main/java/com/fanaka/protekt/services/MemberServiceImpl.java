package com.fanaka.protekt.services;

import com.fanaka.protekt.dao.MemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberDao memberDao;

    @Autowired
    public MemberServiceImpl(MemberDao memberDao) {
        this.memberDao = memberDao;
    }

    @Override
    public Boolean isMember(Long memberId) {
        return memberDao.findMemberById(memberId) != null;
    }
}
