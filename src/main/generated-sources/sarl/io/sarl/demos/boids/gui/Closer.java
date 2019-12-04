/**
 * $Id$
 * 
 * SARL is an general-purpose agent programming language.
 * More details on http://www.sarl.io
 * 
 * Copyright (C) 2014-2019 the original authors or authors.
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
package io.sarl.demos.boids.gui;

import io.sarl.core.OpenEventSpace;
import io.sarl.demos.boids.Die;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.UUID;
import org.eclipse.xtext.xbase.lib.Pure;

@SarlSpecification("0.10")
@SarlElementType(10)
@SuppressWarnings("all")
class Closer extends WindowAdapter {
  private OpenEventSpace space;
  
  private Frame iframe;
  
  public Closer(final Frame parent, final OpenEventSpace comspace) {
    this.space = comspace;
    this.iframe = parent;
  }
  
  /**
   * Clean the simulation asking the agents to die before disposing the window
   */
  @Override
  public void windowClosing(final WindowEvent event) {
    UUID _randomUUID = UUID.randomUUID();
    Die _die = new Die();
    this.space.emit(_randomUUID, _die, null);
    this.iframe.dispose();
  }
  
  @Override
  @Pure
  @SyntheticMember
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }
  
  @Override
  @Pure
  @SyntheticMember
  public int hashCode() {
    int result = super.hashCode();
    return result;
  }
}
