/*
 * Copyright 2014 Effektif GmbH.
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
 * limitations under the License.
 */
package com.effektif.workflow.impl.workflowinstance;

import com.effektif.workflow.api.workflowinstance.VariableInstance;
import com.effektif.workflow.impl.data.DataType;
import com.effektif.workflow.impl.data.TypedValueImpl;
import com.effektif.workflow.impl.workflow.VariableImpl;


/**
 * @author Tom Baeyens
 */
public class VariableInstanceImpl extends BaseInstanceImpl {

  public Object value;
  public VariableImpl variable; // might be null in case variables are created dynamically
  public DataType type;         // never null (initialized with the variable.type)
  public VariableInstanceUpdates updates;

  public VariableInstanceImpl() {
  }

  public VariableInstanceImpl(ScopeInstanceImpl parent, VariableImpl variable, String id) {
    super(parent, id);
    this.variable = variable;
  }

  public VariableInstance toVariableInstance() {
    VariableInstance variableInstance = new VariableInstance();
    variableInstance.setVariableId(variable.id);
    variableInstance.setValue(value);
    variableInstance.setType(type.serialize());
    return variableInstance;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    if (value instanceof TypedValueImpl) {
      throw new RuntimeException("buuuu");
    }
    this.value = value;
    if (updates!=null) {
      updates.isValueChanged = true;
      parent.propagateActivityInstanceChange();
    }
  }

  public void trackUpdates(boolean isNew) {
    updates = new VariableInstanceUpdates(isNew);
  }

  public TypedValueImpl getTypedValue() {
    return new TypedValueImpl(type, value);
  }
}
