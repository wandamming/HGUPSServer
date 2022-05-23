package com.hgups.express.domain.param;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * @author fanc
 * 2020/8/18 0018-14:32
 */
@Data
public class UploadAttachmentParam implements Serializable {

    private MultipartFile file;
    private Integer shippingBatchId;
    private static final long serialVersionUID = 1L;
}
