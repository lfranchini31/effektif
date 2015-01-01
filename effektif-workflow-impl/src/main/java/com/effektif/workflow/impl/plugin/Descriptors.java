/* Copyright 2014 Effektif GmbH.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package com.effektif.workflow.impl.plugin;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.effektif.workflow.api.annotations.Configuration;
import com.effektif.workflow.api.annotations.Label;
import com.effektif.workflow.api.workflow.Activity;
import com.effektif.workflow.api.workflow.Binding;
import com.effektif.workflow.impl.job.JobType;
import com.effektif.workflow.impl.type.BindingType;
import com.effektif.workflow.impl.type.DataType;
import com.effektif.workflow.impl.type.JavaBeanType;
import com.effektif.workflow.impl.type.ListType;
import com.effektif.workflow.impl.type.TextType;
import com.effektif.workflow.impl.util.Exceptions;
import com.effektif.workflow.impl.util.Reflection;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Descriptors {
  
  public List<Descriptor> activityTypeDescriptors = new ArrayList<>();
  public List<Descriptor> dataTypeDescriptors = new ArrayList<>();

  @JsonIgnore
  public ObjectMapper objectMapper;
  @JsonIgnore
  public Map<Type,Descriptor> dataTypeDescriptorsByValueType = new HashMap<>();
  @JsonIgnore
  public Map<Class<?>, Descriptor> activityTypeDescriptorsByClass = new HashMap<>();
  
  // maps activity api configuration classes to activity type implementation classes
  @JsonIgnore
  public Map<Class<?>, Class<? extends ActivityType>> activityTypeClasses = new HashMap<>();

  public Descriptors() {
  }

  public Descriptors(ServiceRegistry serviceRegistry) {
    this.objectMapper = serviceRegistry.getService(ObjectMapper.class);
  }

  public Descriptor registerDataType(DataType dataType) {
    Descriptor descriptor = createTypeDescriptor(dataType);
    addDataTypeDescriptor(descriptor);
    return descriptor;
  }

  public Descriptor registerActivityType(ActivityType activityType) {
    Descriptor descriptor = createTypeDescriptor(activityType);
    addActivityTypeDescriptor(descriptor);
    activityTypeDescriptorsByClass.put(activityType.getClass(), descriptor);
    activityTypeClasses.put(descriptor.configurationClass, activityType.getClass());
    return descriptor;
  }
  
  public ActivityType createActivityType(Activity apiActivity) {
    Class<? extends ActivityType> activityTypeClass = activityTypeClasses.get(apiActivity.getClass());
    try {
      return activityTypeClass.newInstance();
    } catch (Exception e) {
      throw new RuntimeException("Couldn't instantiate "+activityTypeClass+": "+e.getMessage(), e);
    }
  }

  public Descriptor registerJavaBeanType(Class<?> javaBeanClass) {
    Exceptions.checkNotNullParameter(javaBeanClass, "javaBeanClass");
    objectMapper.registerSubtypes(javaBeanClass);
    return registerDataType(new JavaBeanType(javaBeanClass)); 
  }
  
  protected void addDataTypeDescriptor(Descriptor descriptor) {
    dataTypeDescriptors.add(descriptor);
    DataType dataType = descriptor.getDataType();
    objectMapper.registerSubtypes(dataType.getClass());
    Class<?> dataTypeValueClass = dataType.getValueType();
    if (dataTypeValueClass!=null) {
      dataTypeDescriptorsByValueType.put(dataTypeValueClass, descriptor);
      objectMapper.registerSubtypes(dataTypeValueClass);
    }
  }

  public Descriptor createTypeDescriptor(Plugin plugin) {
    Descriptor descriptor = new Descriptor();
    if (plugin instanceof DataType) {
      descriptor.setDataType((DataType) plugin);
    } else if (plugin instanceof ActivityType) {
      descriptor.setActivityType((ActivityType) plugin);
    }
    
    Class<?> pluginClass = plugin.getClass();
    Configuration configurationAnnotation = pluginClass.getAnnotation(Configuration.class);
    if (configurationAnnotation==null) {
      throw new RuntimeException(pluginClass.getName()+" doesn't declare annotation "+Configuration.class.getName());
    }
    Class< ? > configurationClass = configurationAnnotation.value();
    descriptor.setConfigurationClass(configurationClass);
    
    List<Field> fields = Reflection.getNonStaticFieldsRecursive(configurationClass);
    if (!fields.isEmpty()) {
      List<DescriptorField> configurationFields = new ArrayList<DescriptorField>(fields.size());
      descriptor.setConfigurationFields(configurationFields);
      for (Field field : fields) {
        Configuration configuration = field.getAnnotation(Configuration.class);
        if (field.getAnnotation(Configuration.class) != null) {
          Descriptor fieldDescriptor = getDataTypeDescriptor(field);
          DescriptorField descriptorField = new DescriptorField(field, fieldDescriptor.getDataType(), configuration);
          configurationFields.add(descriptorField);
        }
      }
    }
    Label label = pluginClass.getAnnotation(Label.class);
    if (label!=null) {
      descriptor.setLabel(label.value());
    }
    return descriptor;
  }
  
  public Descriptor getDataTypeDescriptor(Field field) {
    return getDataTypeDescriptor(field.getGenericType(), field);
  }

  protected Descriptor getDataTypeDescriptor(Type type, Field field /* passed for error message only */) {
    Descriptor descriptor = dataTypeDescriptorsByValueType.get(type);
    if (descriptor!=null) {
      return descriptor;
    }
    if (String.class.equals(type)) {
      return getDataTypeDescriptor(TextType.class, null);
    } else  if (type instanceof ParameterizedType) {
      ParameterizedType parametrizedType = (ParameterizedType) type;
      Type rawType = parametrizedType.getRawType();
      Type[] typeArgs = parametrizedType.getActualTypeArguments();

      descriptor = createDataTypeDescriptor(rawType, typeArgs, field);
      dataTypeDescriptorsByValueType.put(type, descriptor);
      return descriptor;
    }
    throw new RuntimeException("Don't know how to handle "+type+"'s.  It's used in configuration field: "+field);
  }
  
  protected Descriptor createDataTypeDescriptor(Type rawType, Type[] typeArgs, Field field /* passed for error message only */) {
    if (Binding.class==rawType) {
      Descriptor argDescriptor = getDataTypeDescriptor(typeArgs[0], field);
      BindingType bindingType = new BindingType(argDescriptor.getDataType());
      return new Descriptor(bindingType);
    } else if (List.class==rawType) {
      Descriptor argDescriptor = getDataTypeDescriptor(typeArgs[0], field);
      ListType listType = new ListType(argDescriptor.getDataType());
      return new Descriptor(listType);
    } 
    throw new RuntimeException("Don't know how to handle generic type "+rawType+"'s.  It's used in configuration field: "+field);
  }
  

  public List<Descriptor> getDataTypeDescriptors() {
    return dataTypeDescriptors;
  }

  public List<Descriptor> getActivityTypeDescriptors() {
    return activityTypeDescriptors;
  }

  protected void addActivityTypeDescriptor(Descriptor descriptor) {
    activityTypeDescriptors.add(descriptor);
    objectMapper.registerSubtypes(descriptor.getActivityType().getClass());
  }
  
  public List<DescriptorField> getConfigurationFields(ActivityType activityType) {
    Descriptor descriptor = activityTypeDescriptorsByClass.get(activityType.getClass());
    if (descriptor==null) {
      return null;
    }
    return descriptor.getConfigurationFields();
  }

  public void registerJobType(Class<? extends JobType> jobType) {
    objectMapper.registerSubtypes(jobType);
  }
}
