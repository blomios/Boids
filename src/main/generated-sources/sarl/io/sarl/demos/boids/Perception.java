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
package io.sarl.demos.boids;

import io.sarl.demos.boids.PerceivedBoidBody;
import io.sarl.demos.boids.PerceivedWallBody;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.lang.core.Event;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.xtext.xbase.lib.Pure;
import org.eclipse.xtext.xbase.lib.util.ToStringBuilder;

/**
 * Event from the environment to the boids with the global state of the environment, could be optimized to reduce it to the true local perception of each boids
 * @author Nicolas Gaud
 */
@SarlSpecification("0.10")
@SarlElementType(15)
@SuppressWarnings("all")
public class Perception extends Event {
  public final ConcurrentHashMap<UUID, PerceivedBoidBody> perceivedAgentBody;
  
  public final ConcurrentHashMap<UUID, PerceivedWallBody> perceivedWallBody;
  
  public Perception(final ConcurrentHashMap<UUID, PerceivedBoidBody> bodies, final ConcurrentHashMap<UUID, PerceivedWallBody> walls) {
    ConcurrentHashMap<UUID, PerceivedBoidBody> _concurrentHashMap = new ConcurrentHashMap<UUID, PerceivedBoidBody>(bodies);
    this.perceivedAgentBody = _concurrentHashMap;
    ConcurrentHashMap<UUID, PerceivedWallBody> _concurrentHashMap_1 = new ConcurrentHashMap<UUID, PerceivedWallBody>(walls);
    this.perceivedWallBody = _concurrentHashMap_1;
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
  
  /**
   * Returns a String representation of the Perception event's attributes only.
   */
  @SyntheticMember
  @Pure
  protected void toString(final ToStringBuilder builder) {
    super.toString(builder);
    builder.add("perceivedAgentBody", this.perceivedAgentBody);
    builder.add("perceivedWallBody", this.perceivedWallBody);
  }
  
  @SyntheticMember
  private static final long serialVersionUID = -787339780L;
}
