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
import io.sarl.demos.boids.Die;
import io.sarl.demos.boids.PerceivedBoidBody;
import io.sarl.demos.boids.PerceivedWallBody;
import io.sarl.demos.boids.Perception;
import io.sarl.demos.boids.Population;
import io.sarl.demos.boids.Settings;
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
import javax.inject.Inject;
import org.arakhne.afc.math.geometry.d2.d.Vector2d;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Extension;
import org.eclipse.xtext.xbase.lib.Inline;
import org.eclipse.xtext.xbase.lib.ObjectExtensions;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * A boid agent evolving according C. Reynolds basic behavioral rules
 * @author Nicolas Gaud
 */
@SuppressWarnings("potential_field_synchronization_problem")
@SarlSpecification("0.10")
@SarlElementType(19)
public class Boid extends Agent {
  private UUID environment;
  
  private Vector2d position;
  
  private Vector2d speed;
  
  private Population group;
  
  private double alpha = 0;
  
  private PerceivedWallBody currentWall;
  
  private int currentWallDirection;
  
  private PerceivedBoidBody myBody;
  
  private void $behaviorUnit$Initialize$0(final Initialize occurrence) {
    int _size = ((List<Object>)Conversions.doWrapArray(occurrence.parameters)).size();
    if ((_size > 4)) {
      Object _get = occurrence.parameters[0];
      if ((_get instanceof UUID)) {
        Object _get_1 = occurrence.parameters[0];
        this.environment = ((UUID) _get_1);
      }
      Object _get_2 = occurrence.parameters[1];
      if ((_get_2 instanceof Population)) {
        Object _get_3 = occurrence.parameters[1];
        this.group = ((Population) _get_3);
      }
      Object _get_4 = occurrence.parameters[2];
      if ((_get_4 instanceof Vector2d)) {
        Object _get_5 = occurrence.parameters[2];
        this.position = ((Vector2d) _get_5);
      }
      Object _get_6 = occurrence.parameters[3];
      if ((_get_6 instanceof Vector2d)) {
        Object _get_7 = occurrence.parameters[3];
        this.speed = ((Vector2d) _get_7);
        this.speed.setLength(0.25);
        Vector2d _vector2d = new Vector2d(0, 0.75);
        this.speed.operator_add(_vector2d);
        this.speed.scale(this.group.maxSpeed);
      }
      Object _get_8 = occurrence.parameters[4];
      if ((_get_8 instanceof String)) {
        Logging _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER = this.$castSkill(Logging.class, (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING == null || this.$CAPACITY_USE$IO_SARL_CORE_LOGGING.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING = this.$getSkill(Logging.class)) : this.$CAPACITY_USE$IO_SARL_CORE_LOGGING);
        Object _get_9 = occurrence.parameters[4];
        _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER.setLoggingName((_get_9 == null ? null : _get_9.toString()));
      }
    }
    if (Settings.isLogActivated) {
      Logging _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER_1 = this.$castSkill(Logging.class, (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING == null || this.$CAPACITY_USE$IO_SARL_CORE_LOGGING.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_LOGGING = this.$getSkill(Logging.class)) : this.$CAPACITY_USE$IO_SARL_CORE_LOGGING);
      _$CAPACITY_USE$IO_SARL_CORE_LOGGING$CALLER_1.info("Boids activated");
    }
  }
  
  private void $behaviorUnit$Perception$1(final Perception occurrence) {
    ConcurrentHashMap<UUID, PerceivedBoidBody> boids = occurrence.perceivedAgentBody;
    ConcurrentHashMap<UUID, PerceivedWallBody> walls = occurrence.perceivedWallBody;
    this.myBody = boids.get(this.getID());
    if (((this.myBody != null) && Objects.equal(this.myBody.getOwner(), this.getID()))) {
      this.position = this.myBody.getPosition();
      this.speed = this.myBody.getVitesse();
    }
    Schedules _$CAPACITY_USE$IO_SARL_CORE_SCHEDULES$CALLER = this.$castSkill(Schedules.class, (this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES == null || this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES = this.$getSkill(Schedules.class)) : this.$CAPACITY_USE$IO_SARL_CORE_SCHEDULES);
    final Procedure1<Agent> _function = (Agent it) -> {
      DefaultContextInteractions _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER = this.$castSkill(DefaultContextInteractions.class, (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS == null || this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS = this.$getSkill(DefaultContextInteractions.class)) : this.$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS);
      Action _action = new Action();
      final Procedure1<Action> _function_1 = (Action it_1) -> {
        it_1.influence = this.think(boids.values(), walls.values());
      };
      Action _doubleArrow = ObjectExtensions.<Action>operator_doubleArrow(_action, _function_1);
      class $SerializableClosureProxy implements Scope<Address> {
        
        private final UUID $_environment;
        
        public $SerializableClosureProxy(final UUID $_environment) {
          this.$_environment = $_environment;
        }
        
        @Override
        public boolean matches(final Address it) {
          UUID _uUID = it.getUUID();
          return Objects.equal(_uUID, $_environment);
        }
      }
      final Scope<Address> _function_2 = new Scope<Address>() {
        @Override
        public boolean matches(final Address it) {
          UUID _uUID = it.getUUID();
          return Objects.equal(_uUID, Boid.this.environment);
        }
        private Object writeReplace() throws ObjectStreamException {
          return new SerializableProxy($SerializableClosureProxy.class, Boid.this.environment);
        }
      };
      _$CAPACITY_USE$IO_SARL_CORE_DEFAULTCONTEXTINTERACTIONS$CALLER.emit(_doubleArrow, _function_2);
      if (Settings.isLogActivated) {
      }
    };
    _$CAPACITY_USE$IO_SARL_CORE_SCHEDULES$CALLER.in(Settings.pause, _function);
  }
  
  private void $behaviorUnit$Die$2(final Die occurrence) {
    Lifecycle _$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE$CALLER = this.$castSkill(Lifecycle.class, (this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE == null || this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE.get() == null) ? (this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE = this.$getSkill(Lifecycle.class)) : this.$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE);
    _$CAPACITY_USE$IO_SARL_CORE_LIFECYCLE$CALLER.killMe();
  }
  
  /**
   * The core boids behavior : aggregating all forces into a influence
   */
  protected Vector2d think(final Collection<PerceivedBoidBody> perception, final Collection<PerceivedWallBody> walls) {
    if (((perception != null) || (perception.size() != 0))) {
      Vector2d force = null;
      Vector2d influence = new Vector2d();
      influence.set(0, 0);
      if (this.group.separationOn) {
        force = this.separation(perception);
        force.scale(this.group.separationForce);
        influence.operator_add(force);
      }
      if (this.group.cohesionOn) {
        force = this.cohesion(perception);
        force.scale(this.group.cohesionForce);
        influence.operator_add(force);
      }
      if (this.group.alignmentOn) {
        force = this.alignment(perception);
        force.scale(this.group.alignmentForce);
        influence.operator_add(force);
      }
      if (this.group.repulsionOn) {
        force = this.repulsion(perception);
        force.scale(this.group.repulsionForce);
        influence.operator_add(force);
      }
      double _length = influence.getLength();
      if ((_length > this.group.maxForce)) {
        influence.setLength(this.group.maxForce);
      }
      Vector2d _plus = influence.operator_plus(this.speed);
      force = this.wallEscape(walls, _plus);
      force.scale(100000000.0f);
      influence.scale((1 / this.group.mass));
      Vector2d _multiply = influence.operator_multiply((1 - this.alpha));
      Vector2d _multiply_1 = force.operator_multiply(this.alpha);
      return _multiply.operator_plus(_multiply_1);
    }
    System.out.println("*****************PERCEPT NULL***************************");
    Vector2d force_1 = null;
    Vector2d influence_1 = new Vector2d();
    double direction = Boid.getAngle(this.speed);
    double cos = Math.cos(direction);
    double sin = Math.sin(direction);
    double _x = this.position.getX();
    double x = (_x + ((int) (5 * cos)));
    double _y = this.position.getY();
    double y = (_y + ((int) (5 * sin)));
    Vector2d _vector2d = new Vector2d(x, y);
    Vector2d _minus = _vector2d.operator_minus(this.position);
    influence_1 = _minus;
    force_1 = this.wallEscape(walls, influence_1);
    force_1.scale(100000000.0f);
    return force_1;
  }
  
  /**
   * Determine whether a body is visible or not according to the perception range
   */
  @Pure
  protected boolean isVisible(final PerceivedBoidBody otherBoid, final double distance) {
    Vector2d _position = otherBoid.getPosition();
    Vector2d tmp = _position.operator_minus(this.position);
    double _length = tmp.getLength();
    if ((_length > distance)) {
      return false;
    }
    Vector2d tmp2 = this.speed.clone();
    tmp2.normalize();
    double _multiply = tmp2.operator_multiply(tmp);
    if ((_multiply < this.group.visibleAngleCos)) {
      return false;
    }
    return true;
  }
  
  /**
   * Determine whether a wall's point is visible or not according to the perception range
   */
  @Pure
  protected boolean isVisible(final Vector2d wallPoint) {
    Vector2d tmp = wallPoint.operator_minus(this.position);
    double _length = tmp.getLength();
    if ((_length > Settings.wallPointsMaxDistance)) {
      return false;
    }
    return true;
  }
  
  /**
   * Determine whether a wall between 2 points is in range or not
   */
  @Pure
  protected Vector2d isWallVisible(final Vector2d wallPointA, final Vector2d wallPointB, final Vector2d orientation) {
    Vector2d virtualBoidPoint = this.position.operator_plus(orientation);
    double x1 = 0;
    double x2 = 0;
    double x3 = 0;
    double x4 = 0;
    double b1 = 0;
    double b2 = 0;
    double y1 = 0;
    double y2 = 0;
    double y3 = 0;
    double y4 = 0;
    double a1 = 0;
    double a2 = 0;
    double _x = wallPointA.getX();
    double _x_1 = wallPointB.getX();
    if ((_x < _x_1)) {
      x1 = wallPointA.getX();
      x2 = wallPointB.getX();
      y1 = wallPointA.getY();
      y2 = wallPointB.getY();
    } else {
      x2 = wallPointA.getX();
      x1 = wallPointB.getX();
      y2 = wallPointA.getY();
      y1 = wallPointB.getY();
    }
    double _x_2 = this.position.getX();
    double _x_3 = virtualBoidPoint.getX();
    if ((_x_2 < _x_3)) {
      x3 = this.position.getX();
      x4 = virtualBoidPoint.getX();
      y3 = this.position.getY();
      y4 = virtualBoidPoint.getY();
    } else {
      x4 = this.position.getX();
      x3 = virtualBoidPoint.getX();
      y4 = this.position.getY();
      y3 = virtualBoidPoint.getY();
    }
    a2 = ((y4 - y3) / (x4 - x3));
    b2 = (y3 - (a2 * x3));
    if ((x1 == x2)) {
      double xcomm = x1;
      double ycomm = ((a2 * xcomm) + b2);
      Vector2d pos = new Vector2d(xcomm, ycomm);
      double _length = pos.operator_minus(virtualBoidPoint).getLength();
      double _length_1 = pos.operator_minus(this.position).getLength();
      if ((_length < _length_1)) {
        return pos;
      }
      return null;
    } else {
      a1 = ((y2 - y1) / (x2 - x1));
      b1 = (y1 - (a1 * x1));
      if ((a1 == a2)) {
        return null;
      } else {
        double xcomm_1 = ((b2 - b1) / (a1 - a2));
        double ycomm_1 = ((a1 * xcomm_1) + b1);
        Vector2d pos_1 = new Vector2d(xcomm_1, ycomm_1);
        if (((xcomm_1 >= x1) && (xcomm_1 <= x2))) {
          double _length_2 = pos_1.operator_minus(virtualBoidPoint).getLength();
          double _length_3 = pos_1.operator_minus(this.position).getLength();
          if ((_length_2 < _length_3)) {
            return pos_1;
          }
        }
        return null;
      }
    }
  }
  
  @Pure
  private static double getAngle(final Vector2d v) {
    double zero = 1E-9;
    double _x = v.getX();
    double _x_1 = v.getX();
    if (((_x * _x_1) < zero)) {
      double _y = v.getY();
      if ((_y >= 0)) {
        return (Math.PI / 2);
      }
      return (((-1) * Math.PI) / 2);
    }
    double _x_2 = v.getX();
    if ((_x_2 >= 0)) {
      double _y_1 = v.getY();
      double _x_3 = v.getX();
      return Math.atan((_y_1 / _x_3));
    }
    double _y_2 = v.getY();
    if ((_y_2 >= 0)) {
      double _y_3 = v.getY();
      double _x_4 = v.getX();
      double _atan = Math.atan((_y_3 / _x_4));
      return (Math.PI + _atan);
    }
    double _y_4 = v.getY();
    double _x_5 = v.getX();
    double _atan_1 = Math.atan((_y_4 / _x_5));
    return (_atan_1 - Math.PI);
  }
  
  /**
   * Compute the separation force.
   */
  protected Vector2d separation(final Collection<PerceivedBoidBody> otherBoids) {
    Vector2d force = new Vector2d();
    double len = 0.0;
    for (final PerceivedBoidBody otherBoid : otherBoids) {
      if (((((otherBoid != null) && (!Objects.equal(otherBoid.getOwner(), this.getID()))) && Objects.equal(otherBoid.getGroup(), this.group)) && this.isVisible(otherBoid, this.group.distSeparation))) {
        Vector2d _position = otherBoid.getPosition();
        Vector2d tmp = this.position.operator_minus(_position);
        len = tmp.getLength();
        double _power = Math.pow(len, 2);
        tmp.scale((1.0 / _power));
        force.operator_add(tmp);
      }
    }
    return force;
  }
  
  protected Vector2d wallEscape(final Collection<PerceivedWallBody> walls, final Vector2d orientation) {
    boolean traited = false;
    Vector2d force = new Vector2d(0, 0);
    Vector2d vect = new Vector2d(0, 0);
    Vector2d nearestPoint = null;
    for (final PerceivedWallBody wall : walls) {
      for (int i = 0; (i < ((List<Vector2d>)Conversions.doWrapArray(wall.getPoints())).size()); i++) {
        boolean _isVisible = this.isVisible(wall.getPoints()[i]);
        if (_isVisible) {
          int _size = ((List<Vector2d>)Conversions.doWrapArray(wall.getPoints())).size();
          if (((i + 1) < _size)) {
            Vector2d wallInter = this.isWallVisible(wall.getPoints()[i], wall.getPoints()[(i + 1)], orientation);
            if ((wallInter == null)) {
            } else {
              double _length = this.position.operator_minus(wallInter).getLength();
              if ((_length < Settings.wallPointsMaxDistance)) {
                if (((nearestPoint == null) || (this.position.operator_minus(wallInter).getLength() < this.position.operator_minus(nearestPoint).getLength()))) {
                  nearestPoint = wallInter;
                  Vector2d tmp = wallInter.operator_minus(this.position);
                  Vector2d newTemp = null;
                  if (((this.currentWall != null) && (wall.hashCode() == this.currentWall.hashCode()))) {
                    Vector2d _get = wall.getPoints()[(i + this.currentWallDirection)];
                    Vector2d _minus = _get.operator_minus(wallInter);
                    newTemp = _minus;
                  } else {
                    Vector2d _get_1 = wall.getPoints()[i];
                    Vector2d _minus_1 = _get_1.operator_minus(wallInter);
                    double _angle = tmp.angle(_minus_1);
                    Vector2d _get_2 = wall.getPoints()[(i + 1)];
                    Vector2d _minus_2 = _get_2.operator_minus(wallInter);
                    double _angle_1 = tmp.angle(_minus_2);
                    if ((_angle < _angle_1)) {
                      Vector2d _get_3 = wall.getPoints()[(i + 1)];
                      Vector2d _minus_3 = _get_3.operator_minus(wallInter);
                      newTemp = _minus_3;
                      this.currentWallDirection = 1;
                      this.currentWall = wall;
                    } else {
                      Vector2d _get_4 = wall.getPoints()[i];
                      Vector2d _minus_4 = _get_4.operator_minus(wallInter);
                      newTemp = _minus_4;
                      this.currentWallDirection = 0;
                      this.currentWall = wall;
                    }
                  }
                  Vector2d _get_5 = wall.getPoints()[(i + 1)];
                  Vector2d _minus_5 = _get_5.operator_minus(wallInter);
                  double _abs = Math.abs(Math.sin(tmp.angle(_minus_5)));
                  double _length_1 = tmp.getLength();
                  double dist = (_abs * _length_1);
                  dist = (dist - (Settings.wallPointsMaxDistance / 10));
                  if ((dist < 0.00001)) {
                    dist = 0.00001;
                  }
                  System.out.println(dist);
                  double _power = Math.pow(dist, 2);
                  newTemp.scale((1 / _power));
                  vect = newTemp;
                  this.alpha = (1 - (dist / Settings.wallPointsMaxDistance));
                  traited = true;
                }
              }
            }
          }
        }
      }
    }
    force = vect;
    if ((!traited)) {
      this.alpha = 0;
    }
    return force;
  }
  
  @Pure
  protected double vectDirect(final Vector2d a, final Vector2d b) {
    double _y = b.getY();
    double _y_1 = a.getY();
    double _x = b.getX();
    double _x_1 = a.getX();
    return ((_y - _y_1) / (_x - _x_1));
  }
  
  /**
   * Compute the cohesion force.
   */
  protected Vector2d cohesion(final Collection<PerceivedBoidBody> otherBoids) {
    int nbTot = 0;
    Vector2d force = new Vector2d();
    for (final PerceivedBoidBody otherBoid : otherBoids) {
      if (((((otherBoid != null) && (!Objects.equal(otherBoid.getOwner(), this.getID()))) && Objects.equal(otherBoid.getGroup(), this.group)) && this.isVisible(otherBoid, this.group.distCohesion))) {
        nbTot++;
        Vector2d _position = otherBoid.getPosition();
        force.operator_add(_position);
      }
    }
    if ((nbTot > 0)) {
      force.scale((1.0 / nbTot));
      force.operator_remove(this.position);
    }
    return force;
  }
  
  /**
   * Compute the alignment force.
   */
  protected Vector2d alignment(final Collection<PerceivedBoidBody> otherBoids) {
    int nbTot = 0;
    Vector2d tmp = new Vector2d();
    Vector2d force = new Vector2d();
    for (final PerceivedBoidBody otherBoid : otherBoids) {
      if (((((otherBoid != null) && (!Objects.equal(otherBoid.getOwner(), this.getID()))) && Objects.equal(otherBoid.getGroup(), this.group)) && this.isVisible(otherBoid, this.group.distAlignment))) {
        nbTot++;
        tmp.set(otherBoid.getVitesse());
        double _length = tmp.getLength();
        tmp.scale((1.0 / _length));
        force.operator_add(tmp);
      }
    }
    if ((nbTot > 0)) {
      force.scale((1.0 / nbTot));
    }
    return force;
  }
  
  /**
   * Compute the repulsion force.
   */
  protected Vector2d repulsion(final Collection<PerceivedBoidBody> otherBoids) {
    Vector2d force = new Vector2d();
    double len = 0.0;
    for (final PerceivedBoidBody otherBoid : otherBoids) {
      if (((((otherBoid != null) && (!Objects.equal(otherBoid.getOwner(), this.getID()))) && (!Objects.equal(otherBoid.getGroup(), this.group))) && 
        this.isVisible(otherBoid, this.group.distRepulsion))) {
        Vector2d _position = otherBoid.getPosition();
        Vector2d tmp = this.position.operator_minus(_position);
        len = tmp.getLength();
        double _power = Math.pow(len, 2);
        tmp.scale((1 / _power));
        force.operator_add(tmp);
      }
    }
    return force;
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
  private void $guardEvaluator$Die(final Die occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {
    assert occurrence != null;
    assert ___SARLlocal_runnableCollection != null;
    ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$Die$2(occurrence));
  }
  
  @SyntheticMember
  @PerceptGuardEvaluator
  private void $guardEvaluator$Perception(final Perception occurrence, final Collection<Runnable> ___SARLlocal_runnableCollection) {
    assert occurrence != null;
    assert ___SARLlocal_runnableCollection != null;
    ___SARLlocal_runnableCollection.add(() -> $behaviorUnit$Perception$1(occurrence));
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
    Boid other = (Boid) obj;
    if (!java.util.Objects.equals(this.environment, other.environment)) {
      return false;
    }
    if (Double.doubleToLongBits(other.alpha) != Double.doubleToLongBits(this.alpha))
      return false;
    if (other.currentWallDirection != this.currentWallDirection)
      return false;
    return super.equals(obj);
  }
  
  @Override
  @Pure
  @SyntheticMember
  public int hashCode() {
    int result = super.hashCode();
    final int prime = 31;
    result = prime * result + java.util.Objects.hashCode(this.environment);
    result = prime * result + (int) (Double.doubleToLongBits(this.alpha) ^ (Double.doubleToLongBits(this.alpha) >>> 32));
    result = prime * result + this.currentWallDirection;
    return result;
  }
  
  @SyntheticMember
  public Boid(final UUID parentID, final UUID agentID) {
    super(parentID, agentID);
  }
  
  @SyntheticMember
  @Inject
  @Deprecated
  public Boid(final BuiltinCapacitiesProvider provider, final UUID parentID, final UUID agentID) {
    super(provider, parentID, agentID);
  }
  
  @SyntheticMember
  @Inject
  public Boid(final UUID parentID, final UUID agentID, final DynamicSkillProvider skillProvider) {
    super(parentID, agentID, skillProvider);
  }
}
