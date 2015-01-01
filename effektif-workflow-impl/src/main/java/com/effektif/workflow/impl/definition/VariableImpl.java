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
package com.effektif.workflow.impl.definition;

import com.effektif.workflow.api.workflow.Variable;
import com.effektif.workflow.impl.WorkflowEngineImpl;
import com.effektif.workflow.impl.plugin.Descriptors;
import com.effektif.workflow.impl.type.DataType;
import com.fasterxml.jackson.annotation.JsonIgnore;


public class VariableImpl {

  public Variable apiVariable;
  public String id;
  public DataType dataType;
  public Object initialValue;

  public WorkflowEngineImpl workflowEngine;
  @JsonIgnore
  public WorkflowImpl workflow;  
  @JsonIgnore
  public ScopeImpl parent;

  public Long line;
  public Long column;

  public VariableImpl(Variable apiVariable) {
    this.apiVariable = apiVariable;
  }
  
  public void validate(WorkflowValidator validator) {
    if (id==null || "".equals(id)) {
      validator.addError("Variable does not have an id");
    }
    validator.workflowEngine.getServiceRegistry().getService(Descriptors.class);
    if (variable.dataType!=null) {
      variable.dataType.validate(this);
    } else {
      addError("No data type configured for variable %s", variable.id);
    }

  }

}
