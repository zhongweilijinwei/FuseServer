package com.u8.server.constants;

/**
 * 支付状态
 */
public class PayState {

    public static final int STATE_PRE_PAY = 1;//已下单，未支付也未确认订单的状态
    public static final int STATE_SUC = 2;//支付成功，三方支付平台反馈已支付成功但游戏方未确认的状态
    public static final int STATE_COMPLETE = 3;//支付完成，游戏方确认已发放道具的状态
    public static final int STATE_FAILED = 4;//支付失败，发生在三方支付平台返回支付失败的时候
    public static final int STATE_PAYING = 5;//支付中，未支付但SDK已确认过此订单的状态，支付中的订单不能再次调起三方支付
    public static final int STATE_CANCEL = 6;//支付已取消，用户主动取消支付

}
