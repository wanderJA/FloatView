package com.wander.floatview

/**
 * author wander
 * date 2018/10/16
 *
 */
interface DragStateListener {
    /**
     * 拖动中
     * @param percent 拖动百分比
     */
    fun onDrag(percent: Float)

    fun onClose()
    fun onOpen()
}