package com.benewake.system.redis;

public class SchedulingLockKey {

    //用户级别的锁
    public final static String SCHEDULING_USER_LOCK_KEY = "Scheduling::user:lock";

    //流程执行过程中的锁
    public final static String SCHEDULING_DATA_LOCK_KEY = "Scheduling::Data";

    public final static String SCHEDULING_DATA_LOCK_KEY_STATE = "Scheduling::Data::State";

    public final static String SCHEDULING_ING_LOCK_KEY = "Scheduling::ing";
}
