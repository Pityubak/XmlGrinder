package com.pityubak.xmlgrinder.config;

import com.pityubak.liberator.annotations.Config;
import com.pityubak.liberator.config.ConfigurationService;
import com.pityubak.xmlgrinder.XmlRead;
import com.pityubak.xmlgrinder.service.XmlReadScoutService;
import com.pityubak.xmlgrinder.service.XmlWriteScoutService;
import com.pityubak.xmlgrinder.service.XmlWriteService;
import com.pityubak.liberator.layer.CollectionConfigurationLayer;

/**
 *
 * @author Pityubak
 */
@Config(XmlRead.class)
public class XmlReadConfig implements ConfigurationService {

    @Override
    public void filter(CollectionConfigurationLayer collection) {
        collection.filter(XmlWriteService.class,
                XmlWriteScoutService.class, XmlReadScoutService.class);
    }

}
