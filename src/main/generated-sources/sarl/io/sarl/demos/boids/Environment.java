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

import com.google.common.base.Objects;
import io.sarl.core.DefaultContextInteractions;
import io.sarl.core.Initialize;
import io.sarl.core.Lifecycle;
import io.sarl.core.Logging;
import io.sarl.core.Schedules;
import io.sarl.demos.boids.Action;
import io.sarl.demos.boids.BoidsSimulation;
import io.sarl.demos.boids.Die;
import io.sarl.demos.boids.GuiRepaint;
import io.sarl.demos.boids.PerceivedBoidBody;
import io.sarl.demos.boids.PerceivedWallBody;
import io.sarl.demos.boids.Perception;
import io.sarl.demos.boids.Settings;
import io.sarl.demos.boids.Start;
import io.sarl.lang.annotation.ImportedCapacityFeature;
import io.sarl.lang.annotation.PerceptGuardEvaluator;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.lang.core.Address;
import io.sarl.lang.core.Agent;
import io.sarl.lang.core.BuiltinCapacitiesProvider;
import io.sarl.lang.core.DynamicSkillProvider;
import io.sarl.lang.core.Scope;
import io.sarl.lang.core.Skill;
import io.sarl.lang.util.ClearableReference;
import io.sarl.lang.util.SerializableProxy;
import java.io.ObjectStreamException;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import javax.inject.Inject;
import org.arakhne.afc.math.geometry.d2.d.Vector2d;
import org.eclipse.xtend.lib.annotations.Accessors;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Inline;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * The environmental agent in charge of collecting boids influences and computing the new state of the virtual world
 * @author Nicolas Gaud
 */
@SarlSpecification("0.10")
@SarlElementType(19)
@SuppressWarnings("all")
public class Environment extends Agent {
  @Accessors
  private int width;
  
  @Accessors
  private int height;
  
  @Accessors
  private ConcurrentHashMap<UUID, PerceivedBoidBody> boids;
  
  @Accessors
  private ConcurrentHashMap<UUID, PerceivedWallBody> walls;
  
  @Accessors
  private ConcurrentSkipListSet<UUID> influences;
  
  private void $behaviorUnit$Initialize$0(final Initialize occurrence) {
    Logging _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER = this.$castSkill(Logging.class, (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING == null || this.$CAPACITY_USE$IO_SARL_CORE_LOGGING.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING = this.$getSkill(Logging.class)) : this.$CAPACITY_USE$IO_SARL_CORE_LOGGING);
    _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER.setLoggingName("Environment");
    int _size = ((List<Object>)Conversions.doWrapArray(occurrence.parameters)).size();
    if ((_size > 1)) {
      Object _get = occurrence.parameters[0];
      if ((_get instanceof Integer)) {
        Object _get_1 = occurrence.parameters[0];
        this.height = ((((Integer) _get_1)) == null ? 0 : (((Integer) _get_1)).intValue());
      }
      Object _get_2 = occurrence.parameters[1];
      if ((_get_2 instanceof Integer)) {
        Object _get_3 = occurrence.parameters[1];
        this.width = ((((Integer) _get_3)) == null ? 0 : (((Integer) _get_3)).intValue());
      }
      this.boids = null;
      ConcurrentSkipListSet<UUID> _concurrentSkipListSet = new ConcurrentSkipListSet<UUID>();
      this.influences = _concurrentSkipListSet;
    }
  }
  
  private void $behaviorUnit$Start$1(final Start occurrence) {
    this.boids = occurrence.perceivedAgentBody;
    this.walls = occurrence.perceivedWallBody;
    DefaultContextInteractions _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER = this.$castSkill(DefaultContextInteractions.class, (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS == null || this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS = this.$getSkill(DefaultContextInteractions.class)) : this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS);
    _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER.emit(new GuiRepaint(this.boids));
    DefaultContextInteractions _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER_1 = this.$castSkill(DefaultContextInteractions.class, (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS == null || this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS = this.$getSkill(DefaultContextInteractions.class)) : this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS);
    _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER_1.emit(new Perception(this.boids, this.walls));
  }
  
  private void $behaviorUnit$Action$2(final Action occurrence) {
    synchronized (this.boids) {
      synchronized (this.influences) {
        boolean _containsKey = this.boids.containsKey(occurrence.getSource().getUUID());
        if (_containsKey) {
          this.influences.add(occurrence.getSource().getUUID());
          this.applyForce(occurrence.influence, this.boids.get(occurrence.getSource().getUUID()));
        }
        int _size = this.influences.size();
        int _size_1 = this.boids.size();
        if ((_size == _size_1)) {
          Schedules _$CAPACITY_USE$IO_SARL_CORE_SCHEDULES$CALLER = this.$castSkill(Schedules.class, (this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES == null || this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES = this.$getSkill(Schedules.class)) : this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES);
          final Procedure1<Agent> _function = (Agent it) -> {
            DefaultContextInteractions _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER = this.$castSkill(DefaultContextInteractions.class, (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS == null || this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS = this.$getSkill(DefaultContextInteractions.class)) : this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS);
            class $SerializableClosureProxy implements Scope<Address> {
              
              private final UUID $_id_1;
              
              public $SerializableClosureProxy(final UUID $_id_1) {
                this.$_id_1 = $_id_1;
              }
              
              @Override
              public boolean matches(final Address it) {
                UUID _uUID = it.getUUID();
                return Objects.equal(_uUID, $_id_1);
              }
            }
            final Scope<Address> _function_1 = new Scope<Address>() {
              @Override
              public boolean matches(final Address it) {
                UUID _uUID = it.getUUID();
                return Objects.equal(_uUID, BoidsSimulation.id);
              }
              private Object writeReplace() throws ObjectStreamException {
                return new SerializableProxy($SerializableClosureProxy.class, BoidsSimulation.id);
              }
            };
            _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER.emit(new GuiRepaint(this.boids), _function_1);
            DefaultContextInteractions _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER_1 = this.$castSkill(DefaultContextInteractions.class, (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS == null || this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS = this.$getSkill(DefaultContextInteractions.class)) : this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS);
            _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER_1.emit(new Perception(this.boids, this.walls));
            if (Settings.isLogActivated) {
              Logging _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER = this.$castSkill(Logging.class, (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING == null || this.$CAPACITY_USE$IO_SARL_CORE_LOGGING.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING = this.$getSkill(Logging.class)) : this.$CAPACITY_USE$IO_SARL_CORE_LOGGING);
              _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER.info("New Simulation Step.");
            }
          };
          _$CAPACITY_USE$IO_SARL_CORE_SCHEDULES$CALLER.in(Settings.pause, _function);
          this.influences.clear();
        }
      }
    }
  }
  
  private void $behaviorUnit$Die$3(final Die occurrence) {
    Lifecycle _$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE$CALLER = this.$castSkill(Lifecycle.class, (this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE == null || this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE = this.$getSkill(Lifecycle.class)) : this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE);
    _$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE$CALLER.killMe();
  }
  
  protected void applyForce(final Vector2d force, final PerceivedBoidBody b) {
    double _length = force.getLength();
    if ((_length > b.getGroup().maxForce)) {
      force.setLength(b.getGroup().maxForce);
    }
    Vector2d acceleration = b.getAcceleration();
    acceleration.set(force);
    Vector2d vitesse = b.getVitesse();
    vitesse.operator_add(acceleration);
    double _length_1 = vitesse.getLength();
    if ((_length_1 > b.getGroup().maxSpeed)) {
      vitesse.setLength(b.getGroup().maxSpeed);
    }
    Vector2d position = b.getPosition();
    position.operator_add(vitesse);
    PerceivedBoidBody bb = this.boids.get(b.getOwner());
    bb.setAcceleration(acceleration);
    bb.setVitesse(vitesse);
    bb.setPosition(position);
    this.clampToWorld(b);
  }
  
  /**
   * The world is circular, this function clamps coordinates to stay within the frame
   */
  protected void clampToWorld(final PerceivedBoidBody b) {
    double posX = b.getPosition().getX();
    double posY = b.getPosition().getY();
    if ((posX > (this.width / 2))) {
      posX = (posX - this.width);
    }
    if ((posX < (((-1) * this.width) / 2))) {
      posX = (posX + this.width);
    }
    if ((posY > (this.height / 2))) {
      posY = (posY - this.height);
    }
    if ((posY < (((-1) * this.height) / 2))) {
      posY = (posY + this.height);
    }
    PerceivedBoidBody _get = this.boids.get(b.getOwner());
    Vector2d _vector2d = new Vector2d(posX, posY);
    _get.setPosition(_vector2d);
  }
  
  @Extension
  @ImportedCapacityFeature(Logging.class)
  @SyntheticMember
  private transient ClearableReference<Skill> $CAPACITY_USE$IO_SARL_CORE_LOGGING;
  
  @SyntheticMember
  @Pure
  @Inline(value = "$castSkill(Logging.class, ($0$CAPACITY_USE$IO_SARL_CORE_LOGGING == null || $0$CAPACITY_USE$IO_SARL_CORE_LOGGING.get() == null) ? ($0$CAPACITY_USE$IO_SARL_CORE_LOGGING = $0$getSkill(Logging.class)) : $0$CAPACITY_USE$IO_SARL_CORE_LOGGING)", imported = Logging.class)
  private Logging $CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER() {
    if (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING == null || this.$CAPACITY_USE$IO_SARL_CORE_LOGGING.get() == null) {
      this.$CAPACITY_USE$IO_SARL_CORE_LOGGING = $getSkill(Logging.class);
    }
    return $castSkill(Logging.class, this.$CAPACITY_USE$IO_SARL_CORE_LOGGING);
  }
  
  @Extension
  @ImportedCapacityFeature(DefaultContextInteractions.class)
  @SyntheticMember
  private transient ClearableReference<Skill> $CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS;
  
  @SyntheticMember
  @Pure
  @Inline(value = "$castSkill(DefaultContextInteractions.class, ($0$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS == null || $0$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS.get() == null) ? ($0$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS = $0$getSkill(DefaultContextInteractions.class)) : $0$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS)", imported = DefaultContextInteractions.class)
  private DefaultContextInteractions $CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER() {
    if (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS == null || this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS.get() == null) {
      this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS = $getSkill(DefaultContextInteractions.class);
    }
    return $castSkill(DefaultContextInteractions.class, this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS);
  }
  
  @Extension
  @ImportedCapacityFeature(Schedules.class)
  @SyntheticMember
  private transient ClearableReference<Skill> $CAPACITY_USE$IO_SARL_CORE_SCHEDULES;
  
  @SyntheticMember
  @Pure
  @Inline(value = "$castSkill(Schedules.class, ($0$CAPACITY_USE$IO_SARL_CORE_SCHEDULES == null || $0$CAPACITY_USE$IO_SARL_CORE_SCHEDULES.get() == null) ? ($0$CAPACITY_USE$IO_SARL_CORE_SCHEDULES = $0$getSkill(Schedules.class)) : $0$CAPACITY_USE$IO_SARL_CORE_SCHEDULES)", imported = Schedules.class)
  private Schedules $CAPACITY_USE$IO_SARL_CORE_SCHEDULES$CALLER() {
    if (this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES == null || this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES.get() == null) {
      this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES = $getSkill(Schedules.class);
    }
    return $castSkill(Schedules.class, this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES);
  }
  
  @Extension
  @ImportedCapacityFeature(Lifecycle.class)
  @SyntheticMember
  private transient ClearableReference<Skill> $CAPACITY_USE$IO_SARL_CORE_LIFECYCLE;
  
  @SyntheticMember
  @Pure
  @Inline(value = "$castSkill(Lifecycle.class, ($0$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE == null || $0$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE.get() == null) ? ($0$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE = $0$getSkill(Lifecycle.class)) : $0$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE)", imported = Lifecycle.class)
  private Lifecycle $CAPACITY_USE$IO_SARL_CORE_LIFECYCLE$CALLER() {
    if (this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE == null || this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE.get() == null) {
      this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE = $getSkill(Lifecycle.class);
    }
    return $castSkill(Lifecycle.class, this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE);
  }
  
  @SyntheticMember
  @PerceptGuardEvaluator
  private void $guardEvaluator$Initialize(final Initialize occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {
    assert occurrence != null;
    assert ___SARLlocal_runnableCollection != null;
    ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$Initialize$0(occurrence));
  }
  
  @SyntheticMember
  @PerceptGuardEvaluator
  private void $guardEvaluator$Start(final Start occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {
    assert occurrence != null;
    assert ___SARLlocal_runnableCollection != null;
    ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$Start$1(occurrence));
  }
  
  @SyntheticMember
  @PerceptGuardEvaluator
  private void $guardEvaluator$Action(final Action occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {
    assert occurrence != null;
    assert ___SARLlocal_runnableCollection != null;
    ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$Action$2(occurrence));
  }
  
  @SyntheticMember
  @PerceptGuardEvaluator
  private void $guardEvaluator$Die(final Die occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {
    assert occurrence != null;
    assert ___SARLlocal_runnableCollection != null;
    ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$Die$3(occurrence));
  }
  
  @Override
  @Pure
  @SyntheticMember
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Environment other = (Environment) obj;
    if (other.width != this.width)
      return false;
    if (other.height != this.height)
      return false;
    return super.equals(obj);
  }
  
  @Override
  @Pure
  @SyntheticMember
  public int hashCode() {
    int result = super.hashCode();
    final int prime = 31;
    result = prime * result + this.width;
    result = prime * result + this.height;
    return result;
  }
  
  @SyntheticMember
  public Environment(final UUID parentID, final UUID agentID) {
    super(parentID, agentID);
  }
  
  @SyntheticMember
  @Inject
  @Deprecated
  public Environment(final BuiltinCapacitiesProvider provider, final UUID parentID, final UUID agentID) {
    super(provider, parentID, agentID);
  }
  
  @SyntheticMember
  @Inject
  public Environment(final UUID parentID, final UUID agentID, final DynamicSkillProvider skillProvider) {
    super(parentID, agentID, skillProvider);
  }
  
  @Pure
  protected int getWidth() {
    return this.width;
  }
  
  protected void setWidth(final int width) {
    this.width = width;
  }
  
  @Pure
  protected int getHeight() {
    return this.height;
  }
  
  protected void setHeight(final int height) {
    this.height = height;
  }
  
  @Pure
  protected ConcurrentHashMap<UUID, PerceivedBoidBody> getBoids() {
    return this.boids;
  }
  
  protected void setBoids(final ConcurrentHashMap<UUID, PerceivedBoidBody> boids) {
    this.boids = boids;
  }
  
  @Pure
  protected ConcurrentHashMap<UUID, PerceivedWallBody> getWalls() {
    return this.walls;
  }
  
  protected void setWalls(final ConcurrentHashMap<UUID, PerceivedWallBody> walls) {
    this.walls = walls;
  }
  
  @Pure
  protected ConcurrentSkipListSet<UUID> getInfluences() {
    return this.influences;
  }
  
  protected void setInfluences(final ConcurrentSkipListSet<UUID> influences) {
    this.influences = influences;
  }
}
