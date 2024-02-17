package org.firstinspires.ftc.teamcode.gamma.arm;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.utils.motorUtil;

import java.util.HashMap;

public class arm extends config {
	private final motorUtil motorUtil;

	private double armPower = 0d;
	private double liftPower = 0d;

	private double armSpeedMultiplier = ARM_SPEED;
	private double liftSpeedMultiplier = LIFT_SPEED;

	private int armPosition = 0;
	private int liftPosition = 0;

	private double lastArmPower = 0d;
	private int armTargetPosition = 0;
	private final int ARM_TARGET_TOLERANCE = 5;
	private boolean isBusy = false;

	public arm(final HardwareMap HARDWARE_MAP) {
		this.motorUtil = new motorUtil(
			HARDWARE_MAP.get(DcMotorEx.class, "right_arm"),
			HARDWARE_MAP.get(DcMotorEx.class, "left_arm"),
			HARDWARE_MAP.get(DcMotorEx.class, "lift")
		);

		this.motorUtil.setDirection(
			DcMotorEx.Direction.FORWARD,
			DcMotorEx.Direction.REVERSE,
			DcMotorEx.Direction.FORWARD
		);

		this.motorUtil.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
		this.motorUtil.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

		this.motorUtil.setTargetPositionToTolerance(this.ARM_TARGET_TOLERANCE);

		this.motorUtil.setZeroPowerBehaviour(DcMotorEx.ZeroPowerBehavior.BRAKE);
	}

	public void run(final Gamepad GAMEPAD) {
		this.armPower = GAMEPAD.left_stick_y;
		this.liftPower = GAMEPAD.right_stick_y;
		this.isBusy = this.motorUtil.isBusy(2);


		if (this.armPower == 0 && this.lastArmPower != this.armPower)
			this.armTargetPosition = this.armPosition;

		else if (this.armPower == 0
			|| Math.abs(this.armTargetPosition - this.armPosition) > this.ARM_TARGET_TOLERANCE
		) {
			this.motorUtil.setTargetPosition(
				this.armTargetPosition,
				this.armTargetPosition
			);

			this.motorUtil.setMode(
				DcMotorEx.RunMode.RUN_TO_POSITION,
				DcMotorEx.RunMode.RUN_TO_POSITION
			);

			this.motorUtil.setPower(
				1d,
				1d
			);
		} else
//			if (!this.isBusy)
			this.motorUtil.setMode(
				DcMotorEx.RunMode.RUN_USING_ENCODER,
				DcMotorEx.RunMode.RUN_USING_ENCODER
			);
		this.lastArmPower = this.armPower;

		if (GAMEPAD.right_bumper) this.armSpeedMultiplier = ARM_BOOST;
		else this.armSpeedMultiplier = ARM_SPEED;

		if (GAMEPAD.left_bumper) this.liftSpeedMultiplier = LIFT_SPEED;
		else this.liftSpeedMultiplier = LIFT_BOOST;

		if (this.isBusy
			&& Math.abs(this.armTargetPosition - this.armPosition) > this.ARM_TARGET_TOLERANCE
		) this.armPower = 0.7d;

		this.armPower = Range.clip(this.armPower, -this.armSpeedMultiplier, this.armSpeedMultiplier);
		this.liftPower = Range.clip(this.liftPower, -this.liftSpeedMultiplier, this.liftSpeedMultiplier);

		HashMap<Integer, Integer> positions = this.motorUtil.getCurrentPositions();

		this.armPosition = (positions.get(0) + positions.get(1)) / 2;
		this.liftPosition = positions.get(2);

		this.motorUtil.setPower(
			this.armPower,
			this.armPower,
			this.liftPower
		);
	}

	public void telemetry(final Telemetry TELEMETRY) {
		TELEMETRY.addLine("Arm");
		TELEMETRY.addLine(String.valueOf(this.isBusy));
		TELEMETRY.addLine(this.motorUtil.motors.get(0).getMode().name());
		TELEMETRY.addLine(String.valueOf(this.motorUtil.motors.get(0).getTargetPositionTolerance()));
		TELEMETRY.addLine(String.valueOf(this.armTargetPosition - this.armPosition));

		TELEMETRY.addLine("Power");
		TELEMETRY.addData("Arm: ", this.armPower);
		TELEMETRY.addData("Lift: ", this.liftPower);
		TELEMETRY.addLine();

		TELEMETRY.addLine("Speed multipliers");
		TELEMETRY.addData("Arm: ", this.armSpeedMultiplier);
		TELEMETRY.addData("Lift: ", this.liftSpeedMultiplier);
		TELEMETRY.addLine();

		TELEMETRY.addLine("Positions");
		TELEMETRY.addData("Arm: ", this.armPosition);
		TELEMETRY.addData("Lift: ", this.liftPosition);
		TELEMETRY.addData("Arm target: ", this.armTargetPosition);
		TELEMETRY.addLine();
	}
}