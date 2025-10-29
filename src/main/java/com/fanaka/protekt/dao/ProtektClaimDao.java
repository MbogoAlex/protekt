package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.ProtektClaim;

public interface ProtektClaimDao {
    ProtektClaim createClaim(ProtektClaim claim);
    ProtektClaim updateClaim(ProtektClaim claim);
    ProtektClaim getClaimById(Long id);
}
