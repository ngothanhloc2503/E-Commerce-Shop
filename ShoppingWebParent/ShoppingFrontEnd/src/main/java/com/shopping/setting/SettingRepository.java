package com.shopping.setting;

import com.shopping.common.entity.setting.Setting;
import com.shopping.common.entity.setting.SettingCategory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String> {
    public List<Setting> findByCategory(SettingCategory category);

    public Setting findByKey(String key);
}
