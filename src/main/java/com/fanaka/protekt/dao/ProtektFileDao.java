package com.fanaka.protekt.dao;

import com.fanaka.protekt.entities.ProtektFile;

public interface ProtektFileDao {
    ProtektFile createProtektFile(ProtektFile protektFile);
    ProtektFile updateProtektFile(ProtektFile protektFile);
    ProtektFile findProtektFileById(Long id);
    void deleteProtektFile(Long id);
}