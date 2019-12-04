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

import io.sarl.bootstrap.SRE;
import io.sarl.bootstrap.SREBootstrap;
import io.sarl.core.OpenEventSpace;
import io.sarl.demos.boids.Boid;
import io.sarl.demos.boids.Environment;
import io.sarl.demos.boids.GuiRepaint;
import io.sarl.demos.boids.PerceivedBoidBody;
import io.sarl.demos.boids.PerceivedWallBody;
import io.sarl.demos.boids.Population;
import io.sarl.demos.boids.Settings;
import io.sarl.demos.boids.Start;
import io.sarl.demos.boids.Wall;
import io.sarl.demos.boids.WallParameters;
import io.sarl.demos.boids.gui.EnvironmentGui;
import io.sarl.lang.annotation.SarlElementType;
import io.sarl.lang.annotation.SarlSpecification;
import io.sarl.lang.annotation.SyntheticMember;
import io.sarl.lang.core.AgentContext;
import io.sarl.lang.core.Event;
import io.sarl.lang.core.EventListener;
import io.sarl.lang.core.EventSpace;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.arakhne.afc.math.geometry.d2.Vector2D;
import org.arakhne.afc.math.geometry.d2.d.Vector2d;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Pure;

/**
 * The boids simulation launching the SARL environment with the corresponding agent and ensuring the communication between agents and the GUI
 * @author Nicolas Gaud
 */
@SarlSpecification("0.10")
@SarlElementType(10)
@SuppressWarnings("all")
public class BoidsSimulation implements EventListener {
  public static final UUID id = UUID.randomUUID();
  
  /**
   * SRE Kernel instance
   */
  private SREBootstrap kernel;
  
  /**
   * The default SARL context where environment and boids are spawned
   */
  private AgentContext defaultSARLContext;
  
  /**
   * Identifier of the environment
   */
  private UUID environment;
  
  private int width = Settings.EnvtWidth;
  
  private int height = Settings.EnvtHeight;
  
  /**
   * Map buffering boids before launch/start
   */
  private Map<Population, Integer> boidsToLaunch;
  
  private Map<WallParameters, Integer> wallsToLaunch;
  
  /**
   * Map buffering boids' bodies before launch/start
   */
  private ConcurrentHashMap<UUID, PerceivedBoidBody> boidBodies;
  
  private ConcurrentHashMap<UUID, PerceivedWallBody> wallBodies;
  
  private int boidsCount;
  
  private int wallsCount;
  
  /**
   * Boolean specifying id the simulation is started or not.
   */
  private boolean isSimulationStarted = false;
  
  /**
   * the vent space used to establish communication between GUI and agents,
   * Especially enabling GUI to forward start event to the environment,
   * respectively the environment to send GUIRepain at each simulation step to the GUI
   */
  private OpenEventSpace space;
  
  /**
   * The Graphical user interface
   */
  private EnvironmentGui myGUI;
  
  public BoidsSimulation() {
    this.boidsCount = 0;
    ConcurrentHashMap<UUID, PerceivedBoidBody> _concurrentHashMap = new ConcurrentHashMap<UUID, PerceivedBoidBody>();
    this.boidBodies = _concurrentHashMap;
    ConcurrentHashMap<UUID, PerceivedWallBody> _concurrentHashMap_1 = new ConcurrentHashMap<UUID, PerceivedWallBody>();
    this.wallBodies = _concurrentHashMap_1;
    this.boidsToLaunch = CollectionLiterals.<Population, Integer>newHashMap();
    this.wallsToLaunch = CollectionLiterals.<WallParameters, Integer>newHashMap();
  }
  
  public void start() {
    this.launchAllAgents();
    this.isSimulationStarted = true;
  }
  
  public void stop() {
    this.killAllAgents();
    this.isSimulationStarted = false;
  }
  
  /**
   * Add the boids of a population to the simulation.
   * 
   * @param p - the population to add.
   */
  public void addBoid(final Population p) {
    this.boidsCount++;
    if ((!this.isSimulationStarted)) {
      Integer currentBoidCount = this.boidsToLaunch.get(p);
      if ((currentBoidCount != null)) {
        currentBoidCount++;
      } else {
        Integer _integer = new Integer(1);
        currentBoidCount = _integer;
      }
      this.boidsToLaunch.put(p, currentBoidCount);
    } else {
      this.launchBoid(p, ("Boid" + Integer.valueOf(this.boidsCount)));
    }
  }
  
  public void addWall(final WallParameters p) {
    this.addPointsOnWall(p);
    this.wallsCount++;
    if ((!this.isSimulationStarted)) {
      Integer currentWallCount = this.wallsToLaunch.get(p);
      if ((currentWallCount != null)) {
        currentWallCount++;
      } else {
        Integer _integer = new Integer(1);
        currentWallCount = _integer;
      }
      this.wallsToLaunch.put(p, currentWallCount);
    } else {
      this.launchWall(p, ("Wall" + Integer.valueOf(this.wallsCount)));
    }
  }
  
  public void addPointsOnWall(final WallParameters w) {
    for (int i = 0; (i < (((List<Vector2d>)Conversions.doWrapArray(w.getPoints())).size() - 1)); i++) {
      Vector2d _get = w.getPoints()[i];
      Vector2D<?, ?> _get_1 = w.getPoints()[(i + 1)];
      double _length = _get.operator_minus(_get_1).getLength();
      if ((_length >= Settings.wallPointsMaxDistance)) {
        Vector2d _get_2 = w.getPoints()[i];
        Vector2D<?, ?> _get_3 = w.getPoints()[(i + 1)];
        double _length_1 = _get_2.operator_minus(_get_3).getLength();
        double nbPointsToAdd = (_length_1 / (Settings.wallPointsMaxDistance / 2));
        nbPointsToAdd = Math.ceil(nbPointsToAdd);
        Vector2d _get_4 = w.getPoints()[i];
        Vector2D<?, ?> _get_5 = w.getPoints()[(i + 1)];
        double _length_2 = _get_4.operator_minus(_get_5).getLength();
        double step = (_length_2 / nbPointsToAdd);
        Vector2d _get_6 = w.getPoints()[(i + 1)];
        Vector2D<?, ?> _get_7 = w.getPoints()[i];
        Vector2d vec = _get_6.operator_minus(_get_7);
        vec.normalize();
        vec.scale(step);
        for (int j = 0; (j < (nbPointsToAdd - 1)); j++) {
          {
            Vector2d _get_8 = w.getPoints()[(i + j)];
            Vector2d point = _get_8.operator_plus(vec);
            Vector2d[] _points = w.getPoints();
            final List<Vector2d> myList = new ArrayList<Vector2d>((Collection<? extends Vector2d>)Conversions.doWrapArray(_points));
            System.out.println(point);
            myList.add(((i + j) + 1), point);
            w.setPoints(myList.<Vector2d>toArray(((Vector2d[])Conversions.unwrapArray(myList, Vector2d.class))));
          }
        }
      }
    }
  }
  
  private void launchAllAgents() {
    try {
      this.kernel = SRE.getBootstrap();
      this.defaultSARLContext = this.kernel.startWithoutAgent();
      this.environment = this.kernel.startAgent(Environment.class, Integer.valueOf(this.height), Integer.valueOf(this.width));
      this.launchAllBoids();
      this.launchAllWalls();
      EventSpace _defaultSpace = this.defaultSARLContext.getDefaultSpace();
      this.space = ((OpenEventSpace) _defaultSpace);
      EnvironmentGui _environmentGui = new EnvironmentGui(this.space, this.height, this.width, this.boidBodies, this.wallBodies);
      this.myGUI = _environmentGui;
      this.space.register(this);
      Start _start = new Start(this.boidBodies, this.wallBodies);
      this.space.emit(BoidsSimulation.id, _start, null);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  private void launchAllBoids() {
    int boidNum = 0;
    Set<Map.Entry<Population, Integer>> _entrySet = this.boidsToLaunch.entrySet();
    for (final Map.Entry<Population, Integer> e : _entrySet) {
      for (int i = 0; (i < e.getValue().doubleValue()); i++) {
        {
          boidNum++;
          this.launchBoid(e.getKey(), ("Boid" + Integer.valueOf(boidNum)));
        }
      }
    }
  }
  
  private void launchAllWalls() {
    long wallNum = 0L;
    Set<Map.Entry<WallParameters, Integer>> _entrySet = this.wallsToLaunch.entrySet();
    for (final Map.Entry<WallParameters, Integer> e : _entrySet) {
      for (int i = 0; (i < e.getValue().doubleValue()); i++) {
        {
          wallNum++;
          this.launchWall(e.getKey(), ("Wall" + Long.valueOf(wallNum)));
        }
      }
    }
  }
  
  @SuppressWarnings({ "constant_condition", "discouraged_reference" })
  private void launchBoid(final Population p, final String boidName) {
    try {
      double _random = Math.random();
      double _random_1 = Math.random();
      Vector2d initialPosition = new Vector2d(((_random - 0.5) * this.width), ((_random_1 - 0.5) * this.height));
      double _random_2 = Math.random();
      double _random_3 = Math.random();
      Vector2d initialVitesse = new Vector2d((_random_2 - 0.5), (_random_3 - 0.5));
      UUID b = this.kernel.startAgent(Boid.class, this.environment, p, initialPosition, initialVitesse, boidName);
      PerceivedBoidBody _perceivedBoidBody = new PerceivedBoidBody(p, b, initialPosition, initialVitesse);
      this.boidBodies.put(b, _perceivedBoidBody);
      if (Settings.isLogActivated) {
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @SuppressWarnings({ "constant_condition", "discouraged_reference" })
  private void launchWall(final WallParameters p, final String wallName) {
    try {
      UUID b = this.kernel.startAgent(Wall.class, this.environment, wallName);
      Vector2d[] _points = p.getPoints();
      PerceivedWallBody _perceivedWallBody = new PerceivedWallBody(b, _points);
      this.wallBodies.put(b, _perceivedWallBody);
      if (Settings.isLogActivated) {
      }
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
  
  @Pure
  private void killAllAgents() {
  }
  
  @Override
  @Pure
  public UUID getID() {
    return BoidsSimulation.id;
  }
  
  /**
   * Methods managing event coming from agents
   */
  @Override
  public void receiveEvent(final Event event) {
    if ((event instanceof GuiRepaint)) {
      this.myGUI.setBoids(((GuiRepaint)event).perceivedAgentBody);
      this.myGUI.repaint();
    }
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
    BoidsSimulation other = (BoidsSimulation) obj;
    if (!Objects.equals(this.environment, other.environment)) {
      return false;
    }
    if (other.width != this.width)
      return false;
    if (other.height != this.height)
      return false;
    if (other.boidsCount != this.boidsCount)
      return false;
    if (other.wallsCount != this.wallsCount)
      return false;
    if (other.isSimulationStarted != this.isSimulationStarted)
      return false;
    return super.equals(obj);
  }
  
  @Override
  @Pure
  @SyntheticMember
  public int hashCode() {
    int result = super.hashCode();
    final int prime = 31;
    result = prime * result + Objects.hashCode(this.environment);
    result = prime * result + this.width;
    result = prime * result + this.height;
    result = prime * result + this.boidsCount;
    result = prime * result + this.wallsCount;
    result = prime * result + (this.isSimulationStarted ? 1231 : 1237);
    return result;
  }
}
