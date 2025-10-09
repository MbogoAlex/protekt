package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.Member;

public interface MemberDao {
    Member findMemberById(Long id);
}
