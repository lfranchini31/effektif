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
package com.effektif.workflow.api.activities;

import com.effektif.workflow.api.condition.Condition;
import com.effektif.workflow.api.deprecated.activities.ServiceTask;
import com.effektif.workflow.api.serialization.bpmn.BpmnElement;
import com.effektif.workflow.api.serialization.bpmn.BpmnTypeAttribute;
import com.effektif.workflow.api.serialization.json.TypeName;
import com.effektif.workflow.api.workflow.Transition;


/** 
 * invokes a java method.
 *
 * @see <a href="https://github.com/effektif/effektif/wiki/Java-Service-Task">Java Service Task</a>
 * @author Tom Baeyens
 */
@TypeName("javaServiceTask")
@BpmnElement("serviceTask")
@BpmnTypeAttribute(attribute="type", value="java")
public class JavaServiceTask extends ServiceTask {

  @Override
  public JavaServiceTask id(String id) {
    super.id(id);
    return this;
  }
  
  @Override
  public JavaServiceTask name(String name) {
    super.name(name);
    return this;
  }

  @Override
  public JavaServiceTask description(String description) {
    super.description(description);
    return this;
  }

  @Override
  public JavaServiceTask transitionTo(String toActivityId) {
    super.transitionTo(toActivityId);
    return this;
  }

  @Override
  public JavaServiceTask transitionWithConditionTo(Condition condition, String toActivityId) {
    super.transitionWithConditionTo(condition, toActivityId);
    return this;
  }

  @Override
  public JavaServiceTask transitionToNext() {
    super.transitionToNext();
    return this;
  }

  @Override
  public JavaServiceTask transitionTo(Transition transition) {
    super.transitionTo(transition);
    return this;
  }

  @Override
  public JavaServiceTask transition(Transition transition) {
    super.transition(transition);
    return this;
  }

  @Override
  public JavaServiceTask transition(String id, Transition transition) {
    super.transition(id, transition);
    return this;
  }

  @Override
  public JavaServiceTask property(String key, Object value) {
    super.property(key, value);
    return this;
  }

  @Override
  public JavaServiceTask propertyOpt(String key, Object value) {
    super.propertyOpt(key, value);
    return this;
  }

}
