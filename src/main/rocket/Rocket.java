package rocket;

import util.Entity;
import simulation.World;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.shape.ArcType;



/**
 * A class for storing data about a Rocket body.
*/

public class Rocket extends Entity {
	
	// Physics variables
	private double width = 40;
	private double centerTankWidth = width / 2.5;
	private double height = 100;
	private double noseConeHeight = 30;
	private double centerTankHeight = height - noseConeHeight - 10;
	private double finHeight = 20;
	private double engineConeWidth = centerTankWidth;
	private double engineConeHeight = 10;
	private double turnRate = 60; // degrees per second
	
	// Flight attributes
	private boolean airborne = true;
	private double fuel; 
	private RocketEngine[] engines;
    private ParticleEmitter[] rcsThrusters;
	
	
	private double landingAngleMargin = 10;
	private double acceptableLandingVelocity = 100;
	private double landingVelocity;

	private PIDController pidController;
	private double targetAltitude;

	Rocket() {}
	
	/**
	 * Creates a Rocket controlled with PID with default width, height and color, and parameters
	 * for x and y and fuel.
	 * @param x the middle x coordinate of the Rocket
	 * @param y the top y coordinate of the Rocket
	 * @param fuel the initial fuel amount of the Rocket
	 * @param groundY the y-coordinate of the top of the ground
	 * @param userControlled true if the Rocket is to be controlled by a user
	 */
	public Rocket(double x, double y, double fuel, double groundY) {
		
		super(x, y);
        this.fuel = fuel;
		this.engines = new RocketEngine [] {
				new RocketEngine(groundY, getEngineConeWidth(), getEngineConeHeight(), 
						0, getHeight() - getEngineConeHeight())
        };
		
        this.targetAltitude = groundY;
        this.pidController = new PIDController(0.5, 0.1, 0.001);
        this.pidController.setOutputLimits(0, 250);


		double rcsYoffset = getNoseConeHeight() + getCenterTankHeight() / 2 
				- getFinHeight();
		double rcsXOffset = getCenterTankWidth() / 2 + 4 / 2;
		this.rcsThrusters = new ParticleEmitter [] {
				
				new ParticleEmitter(4, 8, groundY, 
						new Color[] {Color.WHITE}, -rcsXOffset, rcsYoffset, 
						-90, Color.RED),
				new ParticleEmitter(4, 8, groundY, 
						new Color[] {Color.WHITE}, rcsXOffset, rcsYoffset, 
						90, Color.RED)
				
		};
		
	}

	/** 
	 * Gets the width of the rocket
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * Sets the width of the Rocket
	 * @param width the new width of the Rocket
	 */
	public void setWidth(double width) {
		this.width = width;
	}

	/**
	 * Gets the height of the Rocket
	 * @return the height
	 */
	public double getHeight() {
		return height;
	}

	/**
	 * Sets the height of the Rocket
	 * @param height the new height of the Rocket
	 */
	public void setHeight(double height) {
		this.height = height;
	}

	/**
	 * Returns the target altitude of the Rocket
	 * @return the target altitude
	 */
	public double getTargetAltitude() {
		return targetAltitude;
	}

	/**
	 * Sets the target altitude of the Rocket
	 * @param targetAltitude the new target altitude
	 */
	public void setTargetAltitude(double targetAltitude) {
		this.targetAltitude = targetAltitude;
	}

	/**
	 * Gets the width of the Rocket's center tank.
	 * @return the center tank width
	 */
	public double getCenterTankWidth() {
		return centerTankWidth;
	}

	/**
	 * Sets the width of the Rocket's center tank.
	 * @param centerTankWidth the new center tank width
	 */
	public void setCenterTankWidth(double centerTankWidth) {
		this.centerTankWidth = centerTankWidth;
	}

	/**
	 * Gets the height of the Rocket's center tank.
	 * @return the center tank height
	 */
	public double getCenterTankHeight() {
		return centerTankHeight;
	}

	/**
	 * Sets the height of the Rocket's center tank.
	 * @param centerTankWidth the new center tank height
	 */
	public void setCenterTankHeight(double centerTankHeight) {
		this.centerTankHeight = centerTankHeight;
	}

	/**
	 * Gets the height of the Rocket's fins.
	 * @return the Rocket's fin height
	 */
	public double getFinHeight() {
		return finHeight;
	}

	/**
	 * Sets the height of the Rocket's fins.
	 * @param finHeight the new fin height
	 */
	public void setFinHeight(double finHeight) {
		this.finHeight = finHeight;
	}

	/**
	 * Gets the height of the Rocket's nose cone.
	 * @return the nose cone height
	 */
	public double getNoseConeHeight() {
		return noseConeHeight;
	}

	/**
	 * Sets the height of the Rocket's nose cone.
	 * @param noseConeHeight the new nose cone height
	 */
	public void setNoseConeHeight(double noseConeHeight) {
		this.noseConeHeight = noseConeHeight;
	}

	/**
	 * Gets the width of the Rocket's engine cone.
	 * @return the engine cone width
	 */
	public double getEngineConeWidth() {
		return engineConeWidth;
	}

	/**
	 * Sets the width of the Rocket's engine cone.
	 * @param engineConeWidth the new engine cone width
	 */
	public void setEngineConeWidth(double engineConeWidth) {
		this.engineConeWidth = engineConeWidth;
	}

	/**
	 * Gets the height of the Rocket's engine cone.
	 * @return the engine cone height
	 */
	public double getEngineConeHeight() {
		return engineConeHeight;
	}

	/**
	 * Sets the height of the Rocket's engine cone.
	 * @param engineConeHeight the new engine cone height
	 */
	public void setEngineConeHeight(double engineConeHeight) {
		this.engineConeHeight = engineConeHeight;
	}

	/**
	 * Gets the Rocket's rotation speed, in degrees per second
	 * @return the turn rate in degrees per second
	 */
	public double getTurnRate() {
		return turnRate;
	}

	/**
	 * Sets the rotation speed of the Rocket
	 * @param turnRate a rotation speed, in degrees per second
	 */
	public void setTurnRate(double turnRate) {
		this.turnRate = turnRate;
	}

	/**
	 * Gets the amount of fuel left in the Rocket
	 * @return the fuel left
	 */
	public double getFuel() {
		return fuel;
	}

	/**
	 * Sets the amount of fuel left in the Rocket
	 * @param fuel the new fuel amount
	 */
	public void setFuel(double fuel) {
		this.fuel = fuel;
	}

	/**
	 * Gets the RocketEngines associated with this Rocket
	 * @return the Rocket's engines
	 */
	public RocketEngine[] getEngines() {
		return engines;
	}

	/**
	 * Sets the RocketEngines associated with this Rocket
	 * @param engines the new engines for the Rocket
	 */
	public void setEngines(RocketEngine[] engines) {
		this.engines = engines;
	}

	/**
	 * Gets the ParticleEmitter objects representing RCS thrusters for the 
	 * Rocket.
	 * @return the Rocket's RCS thrusters as ParticleEmitters
	 */
	public ParticleEmitter[] getRCSThrusters() {
		return rcsThrusters;
	}
	
	/**
	 * Sets the ParticleEmitter objects representing RCS thrusters for the 
	 * Rocket.
	 * @param rcsThrusters the new RCS thruster ParticleEmitters
	 */
	public void setRCSThrusters(ParticleEmitter[] rcsThrusters) {
		this.rcsThrusters = rcsThrusters;
	}

	/**
	 * Returns true if the Rocket is airborne, false otherwise
	 * @return the airborne status of the Rocket
	 */
	public boolean isAirborne() {
		return airborne;
	}

	/**
	 * Sets the airborne status of the Rocket
	 * @param airborne the new status
	 */
	public void setAirborne(boolean airborne) {
		this.airborne = airborne;
	}
	
	/**
	 * Gets the maximum deviation from 90 degrees that the Rocket can have when
	 * landing and still have the landing be considered safe.
	 * @return the landing angle margin, in degrees
	 */
	public double getLandingAngleMargin() {
		return landingAngleMargin;
	}

	/**
	 * Sets the maximum deviation from 90 degrees that the Rocket can have when
	 * landing and still have the landing be considered safe.
	 * @param landingAngleMargin the landing angle margin, in degrees
	 */
	public void setLandingAngleMargin(double landingAngleMargin) {
		this.landingAngleMargin = landingAngleMargin;
	}

	/**
	 * Gets the maximum velocity the Rocket can have when landing and still
	 * have the landing be considered safe.
	 * @return the highest acceptable landing velocity
	 */
    public double getAcceptableLandingVelocity() {
        return acceptableLandingVelocity;
    }
    
	
	/**
	 * Sets the maximum velocity the Rocket can have when landing and still
	 * have the landing be considered safe.
	 * @param acceptableLandingVelocity the highest acceptable landing velocity
	 */
	public void setAcceptableLandingVelocity(double acceptableLandingVelocity) {
		this.acceptableLandingVelocity = acceptableLandingVelocity;
	}




	/**
	 * Gets the magnitude of the velocity the Rocket had on its most recent 
	 * landing.
	 * @return the Rocket's landing velocity
	 */
	public double getLandingVelocity() {
		return this.landingVelocity;
	}

	/**
	 * Sets the magnitude of the velocity the Rocket had on its most recent 
	 * landing.
	 * @param landingVelocity the Rocket's landing velocity
	 */
	public void setLandingVelocity(double landingVelocity) {
		this.landingVelocity = landingVelocity;
	}

	/**
	 * Applies the force of gravity to the Rocket's velocity vector
	 * @param timeElapsed the time, in seconds, since the last tick
	 */
	public void applyGravity(double timeElapsed) {
		
		getVelocity().setY(getVelocity().getY() + World.GRAVITY * timeElapsed);
		
	}
	
	/**
	 * Applies the force of each engine's thrust to the Rocket's velocity 
	 * vector. If an engine is off or there is no fuel left, no force is added. 
	 * @param timeElapsed the time, in seconds, since the last tick
	 */
	public void applyThrust(double timeElapsed) {
		
		if (getFuel() > 0) {
			
			for (RocketEngine engine : getEngines()) {
				
				if (engine.isOn()) {
					
					getVelocity().setX(getVelocity().getX() + 
							Math.cos(Math.toRadians(getDirection())) * 
							engine.getThrustPower() * timeElapsed
							);
							
						getVelocity().setY(getVelocity().getY() +
								Math.sin(Math.toRadians(getDirection())) * -1 *
								engine.getThrustPower() * timeElapsed
								);
				
						setFuel(getFuel() - 
								engine.getFuelBurnRate() * timeElapsed
								);
					
				}
				
			}
			
		} else {
			
			setEnginesOn(false);
			
		}
		
	}
	
	/**
	 * Rotates the Rocket as fast as it can(as defined by turnRate) towards
	 * the angle defined by targetAngle
	 * @param targetAngle the angle to rotate towards, in degrees
	 * @param timeElapsed the time, in seconds, since the last tick
	 */
	protected void pointInDirection(double targetAngle, double timeElapsed) {
		
		double distanceToTargetAngle = targetAngle - getDirection();
		
		if (distanceToTargetAngle < 0 && getVelocity().getY() > 0) {
			
			getRCSThrusters()[0].setOn(true);
			getRCSThrusters()[1].setOn(false);
			
		} else if (distanceToTargetAngle != 0 && getVelocity().getY() > 0){
			
			getRCSThrusters()[0].setOn(false);
			getRCSThrusters()[1].setOn(true);
			
		}
		
		if (Math.abs(distanceToTargetAngle) <= getTurnRate() * timeElapsed) {
			
			setDirection(targetAngle);
			
		} else if (getVelocity().getY() > 0){
			
			if (distanceToTargetAngle < 0) {

				setDirection(getDirection() - getTurnRate() * timeElapsed);

			} else {

				setDirection(getDirection() + getTurnRate() * timeElapsed);

			}
			
		}
		
	}
	
	/**
	 * Sets the 'on' state of all the Rocket's engines to the given state
	 * @param state the new 'on' state for all the Rocket's RocketEngines
	 */
	public void setEnginesOn(boolean state) {
		
		for (RocketEngine engine : getEngines()) {
			
			engine.setOn(state);
			
		}
		
	}
	
	/**
	 * Stops the rocket by setting conditions and variables associated with its
	 * flight to appropriate values
	 */
	public void stop() {
		
		if (isAirborne()) {

			// Turn off visual effects
			setAirborne(false);
			setEnginesOn(false);

			for (ParticleEmitter rcsThruster : getRCSThrusters()) {

				rcsThruster.setOn(false);

			}

			setLandingVelocity(getVelocity().getMagnitude());

			if (
				getVelocity().getMagnitude() < getAcceptableLandingVelocity() &&
				Math.abs(getDirection() - 90) <= getLandingAngleMargin()
			) {
				// Good landing, make the Rocket point straight up
				setDirection(90);

			} 

			getVelocity().setX(0);
			getVelocity().setY(0);

		}
		
	}

	@Override
	public void tick(double timeElapsed) {

		pidController.reset();

		if (isAirborne()) {
			
			double currentAltitude = getY();
            double error = targetAltitude - currentAltitude;
			double controlOutput = pidController.compute(error, timeElapsed);
			
			// Automatic hoverslam
			setEnginesOn(true);

			pointInDirection(getVelocity().getDirection() - 180, timeElapsed);

			for (RocketEngine engine : getEngines()) {
				engine.setThrustPower(controlOutput);
				engine.setOn(true);
			}
			
			applyThrust(timeElapsed);
			applyForces(timeElapsed); // should be last
			
		}
		
		for (RocketEngine engine : getEngines()) {
			
			engine.tick(timeElapsed);
			
		}
		
		for (ParticleEmitter rcsThruster : getRCSThrusters()) {
			
			rcsThruster.tick(timeElapsed);
			
		}
		
	}
	
	/**
	 * Pivots the GraphicsContext around the center of the Rocket so that the
	 * Rocket can be drawn at an angle. This transform should be reversed
	 * (with a save() and restore()) before objects that should be drawn 
	 * normally on the Canvas are drawn.
	 * @param gc the GraphicsContext to rotate
	 */
	public void rotateGraphicsContext(GraphicsContext gc) {
		
		double pivotX = getX();
		double pivotY = getY() + (getHeight() / 2.0);
		Rotate rotate = new Rotate(90 - getDirection(), pivotX, pivotY);
		gc.transform(new Affine(rotate));
		
	}

	/**
	 * Draws Rocket's fins at the bottom of its center tank.
	 * @param gc the GraphicsContext of the Canvas to draw the Rocket on
	 */
	public void drawFins(GraphicsContext gc) {
		
		double [] fin1xPoints = new double[] {
				getX() - getCenterTankWidth() / 2,
				getX() - getCenterTankWidth() / 2,
				getX() - getWidth() / 2
		};
		
		double [] fin2xPoints = new double[] {
				getX() + getCenterTankWidth() / 2,
				getX() + getCenterTankWidth() / 2,
				getX() + getWidth() / 2
		};
		
		double finStartY = getY() + getNoseConeHeight() + getCenterTankHeight() - getFinHeight();
		double [] finyPoints = new double[] {
				finStartY,
				finStartY + getFinHeight(),
				finStartY + getFinHeight()
		};
		
		gc.setFill(Color.BLUE);
		gc.fillPolygon(fin1xPoints, finyPoints, finyPoints.length);
		gc.fillPolygon(fin2xPoints, finyPoints, finyPoints.length);

		
	}
	
	@Override
	public void draw(GraphicsContext gc) {
		
		gc.save();
		
		rotateGraphicsContext(gc);
		
		for (RocketEngine engine : getEngines()) {
			
			engine.alignWith(this);
			engine.draw(gc);
			
		}

		for (ParticleEmitter thruster : getRCSThrusters()) {

			thruster.alignWith(this);
			thruster.draw(gc);

		}

		gc.setFill(getColor());
		
		// Rocket nose cone
		gc.fillArc(getX() - getCenterTankWidth() / 2, getY(), 
				getCenterTankWidth(), getNoseConeHeight() * 2, 
				0, 180, ArcType.ROUND);
		
		// Center tank
		gc.fillRect(getX() - getCenterTankWidth() / 2, 
				getY() + getNoseConeHeight(), 
				getCenterTankWidth(), getCenterTankHeight());
		
		drawFins(gc);
		
		gc.restore();
		
	}
	
}