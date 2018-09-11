package com.egzosn.validator.validator.constraintvalidators;

import com.egzosn.validator.validator.constraints.SingleFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author ZaoSheng
 *         文件合法验证
 */
public class SingleFileUploadValidate implements ConstraintValidator<SingleFileUpload, MultipartFile> {

    private static final Logger LOG = LoggerFactory.getLogger(SingleFileUploadValidate.class);

    private final static Map<String, Object> map = new HashMap<String, Object>();
    private final static String configPath = "upload-config.properties";
    private static String _contentTypeKey = "_allow_content_type";
    private static String _sizeKey = "_allow_size_kb";

    private long size;
    private String[] contentTypes;
    private String useConfig;
    private boolean allowEmpty;

    static {
        Properties config = new Properties();
        InputStream is = null;
        try {
                 is = SingleFileUploadValidate.class.getResourceAsStream(configPath);
            config.load(is);
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (null != is){
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (String key :   config.stringPropertyNames()){
                if (key.endsWith(_contentTypeKey)) {
                    map.put(key, config.getProperty(key));
                } else if (key.endsWith(_sizeKey)) {
                    map.put(key, config.getProperty(key));
                }
            }

    }



    @Override
    public void initialize(SingleFileUpload uploadFileValidate) {
        this.allowEmpty = uploadFileValidate.allowEmpty();
        this.size = uploadFileValidate.size();
        this.contentTypes = uploadFileValidate.contentTypes();
        this.useConfig = uploadFileValidate.useConfig();
        if (!(null == contentTypes || "".equals(contentTypes)) && useConfig != null && !"".equals(useConfig.trim())) {
            String[] _contentTypes = (String[]) map.get(useConfig + _contentTypeKey);
            String sizeStr = (String) map.get(useConfig + _sizeKey);
            if (_contentTypes != null) {
                contentTypes = _contentTypes;
            }
            if (sizeStr != null) {
                size = Long.valueOf(sizeStr);
            }
        }
        this.size = size * 1024;
    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {

        System.out.println(multipartFile.getContentType());
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return allowEmpty;
        }

        return (allowSize(multipartFile) && allowContentType(multipartFile));
       /* if (size == 0 || multipartFile.getSize() <= size) {
            if (contentTypes.length > 0) {
                for (int i = 0; i < contentTypes.length; i++) {
                    if (contentTypes[i].equals(multipartFile.getContentType())) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }*/
    }

    private boolean allowSize(MultipartFile multipartFile) {

        return (size == 0 || multipartFile.getSize() <= size);
    }

    private boolean allowContentType(MultipartFile multipartFile) {
        // 不限制
        if (contentTypes.length == 0)
            return true;
        for (int i = 0; i < contentTypes.length; i++) {
            if (contentTypes[i].equals(multipartFile.getContentType())) {
                return true;
            }
        }
        return false;
    }


}
