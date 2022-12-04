package com.jyh.content.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jiangyiheng
 * @date 2022-10-04 16:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorBody {
    private String body;
    private int status;
}
