package org.firstinspires.ftc.teamcode.main;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Deprecated
public class movement4wd {
	private final LinearOpMode opMode;

	private DcMotor rightFront;
	private DcMotor leftFront;
	private DcMotor rightRear;
	private DcMotor leftRear;

	public movement4wd(final LinearOpMode _opMode) {
		opMode = _opMode;
	}


	public void run() {
		double normalizer;

		double x = opMode.gamepad1.left_stick_x;
		double y = -opMode.gamepad1.left_stick_y;
		double angle = opMode.gamepad1.right_stick_x;

		double rightFrontPower = -x + y - angle;
		double leftFrontPower = x + y + angle;
		double rightRearPower = x + y - angle;
		double leftRearPower = -x + y + angle;

		normalizer = Math.max(
			Math.max(Math.abs(rightFrontPower), Math.abs(leftFrontPower)),
			Math.max(Math.abs(rightRearPower), Math.abs(leftRearPower))
		);

		if (normalizer > 1) {
			rightFrontPower /= normalizer;
			leftFrontPower /= normalizer;
			rightRearPower /= normalizer;
			leftRearPower /= normalizer;
		}

		rightFront.setPower(rightFrontPower);
		leftFront.setPower(leftFrontPower);
		rightRear.setPower(rightRearPower);
		leftRear.setPower(leftRearPower);

		opMode.telemetry.addData("Front right/left: ", "%.2/%.2", rightFrontPower, leftFrontPower);
		opMode.telemetry.addData("Rear right/left: ", "%.2/%.2", rightRearPower, leftRearPower);
		opMode.telemetry.update();
	}

	public void init() {
		rightFront = opMode.hardwareMap.get(DcMotor.class, "right_front");
		leftFront = opMode.hardwareMap.get(DcMotor.class, "left_front");
		rightRear = opMode.hardwareMap.get(DcMotor.class, "right_rear");
		leftRear = opMode.hardwareMap.get(DcMotor.class, "left_rear");

		rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
		leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
		rightRear.setDirection(DcMotorSimple.Direction.FORWARD);
		leftRear.setDirection(DcMotorSimple.Direction.REVERSE);
	}
}
