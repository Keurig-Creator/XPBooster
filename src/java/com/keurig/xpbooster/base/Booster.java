package com.keurig.xpbooster.base;


import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Getter
@Setter
public class Booster {

    protected String id;
    protected String name;
    private int multiplier;
    private String time;
    private Voucher voucher;

}
