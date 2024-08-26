package com.movtery.pojavzh.extra

class ZHExtraConstants {
    companion object {
        //用于监听外置登录
        const val OTHER_LOGIN_TODO: String = "other_login_todo"
        //用于监听离线登录
        const val LOCAL_LOGIN_TODO: String = "local_login_todo"
        //用于监听安装本地整合包事件
        const val INSTALL_LOCAL_MODPACK: String = "install_local_modpack"
        //用于监听版本选择的事件
        const val VERSION_SELECTOR: String = "version_selector"
        //用于监听账号更新事件
        const val ACCOUNT_UPDATE: String = "account_update"
        //用于监听页面不透明度设置
        const val PAGE_OPACITY_CHANGE: String = "page_opacity_change"
    }
}
