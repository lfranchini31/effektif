/* Copyright (c) 2014, Effektif GmbH.
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
package com.effektif.workflow.impl;

import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.BaseVariableResolverFactory;


/**
 * @author Tom Baeyens
 */
public class ExpressionVariableResolver extends BaseVariableResolverFactory {

  @Override
  public VariableResolver createVariable(String name, Object value) {
    return null;
  }

  @Override
  public VariableResolver createVariable(String name, Object value, Class< ? > type) {
    return null;
  }

  @Override
  public boolean isTarget(String name) {
    return false;
  }

  @Override
  public boolean isResolveable(String name) {
    return false;
  }

}
