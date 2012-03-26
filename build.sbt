name := "Akka2Bench"

version := "1.0"

scalaVersion := "2.9.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
	"com.typesafe.akka" % "akka-actor" % "2.0",
	"com.typesafe.akka" % "akka-remote" % "2.0",
	"com.typesafe.akka" % "akka-zeromq" % "2.0",
	"com.typesafe.akka" % "akka-testkit" % "2.0",
	"com.typesafe.akka" % "akka-kernel" % "2.0"
)

libraryDependencies += "org.zeromq" %% "zeromq-scala-binding" % "0.0.5"