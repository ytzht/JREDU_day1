package com.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig vedioDaoConfig;

    private final VedioDao vedioDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        vedioDaoConfig = daoConfigMap.get(VedioDao.class).clone();
        vedioDaoConfig.initIdentityScope(type);

        vedioDao = new VedioDao(vedioDaoConfig, this);

        registerDao(Vedio.class, vedioDao);
    }
    
    public void clear() {
        vedioDaoConfig.getIdentityScope().clear();
    }

    public VedioDao getVedioDao() {
        return vedioDao;
    }

}
