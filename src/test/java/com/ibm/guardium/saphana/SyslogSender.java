package com.ibm.guardium.saphana;

import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.github.javafaker.Faker;

public class SyslogSender {

	public static void main(String[] args) throws Exception {

		selectTest("192.168.0.1", 10514);
//		badLoginTest("192.168.0.1", 10514);

	}

	private static void selectTest(String host, int port) throws Exception {
		Faker faker = new Faker();

		String activeUser = faker.name().username();
		String program = "HDB_TENANTDB";
		String dateTime = new SimpleDateFormat("MMM dd HH:mm:ss").format(new Date());
		String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
		int clientPort = 10_000 + new Random().nextInt(30_000);
		int clientProcessId = 2000 + new Random().nextInt(40_000);
		int sessionId = 100_000 + new Random().nextInt(100_000);
		String sourceProgram = "SOURCE_PROGRAM_2";
		String osUser = "ahmed";
		String sql = String.format("SELECT * FROM %s", faker.internet().domainWord());
		String clientIp=faker.internet().privateIpV4Address();

		// Sending connect message
		sendSyslogTcp(host, port, String.format(
				"<86>%s redhat79 %s[17804]: %s;indexserver;redhat79;HXE;90;39003;HXE;%s;DESKTOP-4NAPAOI;%d;%d;Guardium_Connect;INFO;CONNECT;%s;;;;;;SYSTEM;SUCCESSFUL;;;;;;;;%s;%s;;;;;SYSTEM;%s;SYSTEM;;;;;;;;;;;;;;",
				dateTime, program, isoDate, clientIp, clientProcessId, clientPort, activeUser, sessionId, osUser, sourceProgram));

		// Sending select message
		sendSyslogTcp(host, port, String.format(
				"<86>%s redhat79 %s[17804]: %s;indexserver;redhat79;HXE;90;39003;HXE;%s;DESKTOP-4NAPAOI;%d;%d;GUARDIUM_POLICY;INFO;SELECT;%s;SYSTEM;PERSONS;;;;;SUCCESSFUL;;;;;;;%s;%s;%s;;;;;SYSTEM;%s;SYSTEM;;;;;;;;;;;;;;",
				dateTime, program, isoDate, clientIp, clientProcessId, clientPort, activeUser, sql, sessionId, osUser,
				sourceProgram));

	}

	private static void badLoginTest(String host, int port) throws Exception {
		Faker faker = new Faker();

		String activeUser = faker.name().username();
		String program = "HDB_TENANTDB";
		String dateTime = new SimpleDateFormat("MMM dd HH:mm:ss").format(new Date());
		String isoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date());
		int clientPort = 10_000 + new Random().nextInt(30_000);
		int clientProcessId = 2000 + new Random().nextInt(40_000);
		int sessionId = 100_000 + new Random().nextInt(100_000);
		String sourceProgram = "SOURCE_PROGRAM_2";
		String osUser = "ahmed";
		String exceptionType = "CONNECT";
		String clientIp=faker.internet().privateIpV4Address();
		
		sendSyslogTcp(host, port, String.format(
				"<86>%s redhat79 %s[1886]: %s;indexserver;redhat79;HXE;90;39003;HXE;%s;DESKTOP-4NAPAOI;%d;%d;GUARDIUM_POLICY2;INFO;%s;;;;;;;%s;UNSUCCESSFUL;;;;;;authentication failed;;%d;%s;;;;;;%s;;;;;;;;;;;;;;;",
				dateTime, program, isoDate, clientIp, clientProcessId, clientPort, exceptionType, activeUser, sessionId, osUser,
				sourceProgram));
	}

	private static void sendSyslogTcp(String host, int port, String msg) throws Exception {
		System.out.format("Sending syslog message to %s via TCP on port %d:%n%s%n%n", host, port, msg);

		Socket socket = new Socket(host, port);
		PrintWriter pw = new PrintWriter(socket.getOutputStream());
		pw.write(msg);
		pw.flush();
		pw.close();
		socket.close();
	}

}
