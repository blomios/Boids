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
import io.sarl.demos.boids.PerceivedBoidBody;
import io.sarl.demos.boids.PerceivedWallBody;
import io.sarl.demos.boids.gui.Closer;
import io.sarl.demos.boids.gui.EnvironmentGuiPanel;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.Map;
import java.util.UUID;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * Graphical user interface for boids.
 * 
 * @author Nicolas Gaud
 */
@SarlSpecification("0.10")
@SarlElementType(10)
@SuppressWarnings("all")
public class EnvironmentGui extends Frame {
  private Closer handler;
  
  private EnvironmentGuiPanel panel;
  
  public EnvironmentGui(final OpenEventSpace comspace, final int iheight, final int iwidth, final Map<UUID, PerceivedBoidBody> iboids, final Map<UUID, PerceivedWallBody> iwalls) {
    super();
    Closer _closer = new Closer(this, comspace);
    this.handler = _closer;
    EnvironmentGuiPanel _environmentGuiPanel = new EnvironmentGuiPanel(iheight, iwidth, iboids, iwalls);
    this.panel = _environmentGuiPanel;
    this.setTitle("Boids Simulation");
    this.setSize(iwidth, iheight);
    this.addWindowListener(this.handler);
    this.add("Center", this.panel);
    this.setVisible(true);
  }
  
  public void setBoids(final Map<UUID, PerceivedBoidBody> boids) {
    this.panel.setBoids(boids);
  }
  
  @Override
  public void paint(final Graphics g) {
    super.paint(g);
    this.panel.paint(g);
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
  
  @SyntheticMember
  private static final long serialVersionUID = 779762434L;
}
