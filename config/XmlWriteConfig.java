/*
 * The MIT License
 *
 * Copyright 2020 Pityubak.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.pityubak.xmlgrinder.config;

import com.pityubak.liberator.annotations.Config;
import com.pityubak.liberator.config.ConfigurationService;
import com.pityubak.liberator.misc.Insertion;
import com.pityubak.xmlgrinder.XmlWrite;
import com.pityubak.xmlgrinder.repository.CounterResetable;
import com.pityubak.xmlgrinder.repository.Establishing;
import com.pityubak.xmlgrinder.repository.NodeCountable;
import com.pityubak.xmlgrinder.repository.Writeable;
import com.pityubak.xmlgrinder.service.XmlReadScoutService;
import com.pityubak.xmlgrinder.service.XmlReadService;
import com.pityubak.xmlgrinder.service.XmlWriteScoutService;
import com.pityubak.xmlgrinder.service.XmlWriteService;
import com.pityubak.liberator.layer.CollectionConfigurationLayer;
import com.pityubak.liberator.layer.MethodConfigurationLayer;

/**
 *
 * @author Pityubak
 */
@Config(XmlWrite.class)
public class XmlWriteConfig implements ConfigurationService {

    @Override
    public void filter(CollectionConfigurationLayer collection) {
        ConfigurationService.super.filter(collection); 
        
        collection.filter(XmlWriteScoutService.class, XmlReadService.class, XmlReadScoutService.class);
        
    }
    
    @Override
    public void registerAbstractMethod(MethodConfigurationLayer handler) {
        ConfigurationService.super.registerAbstractMethod(handler); 

        handler
                .registrate(XmlWriteService.class, Writeable.class, Insertion.AFTER_LOW)
                .registrate(XmlWriteService.class, Establishing.class, Insertion.PER_CLASS_HIGH)
                .registrate(XmlWriteService.class, CounterResetable.class, Insertion.BEFORE_NORMAL)
                .registrate(XmlWriteService.class, NodeCountable.class, Insertion.PER_CLASS_NORMAL);
    }
    
}
