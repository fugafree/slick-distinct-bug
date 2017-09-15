import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object SlickDistinctBug extends App {

  def runAndWait[T](dbio: DBIO[T]): T = {
    Await.result(db.run(dbio), Duration.Inf)
  }

  val db = Database.forConfig("h2mem1")
  try {

    println("\n\n####### Start of test #######")

    val users: TableQuery[Users] = TableQuery[Users]

    val setupAction: DBIO[Unit] = DBIO.seq(
      users.schema.create,
      users += User(
        name = "John Doe",
        height = 167,
        hasDog = true,
        city = "Meadows"),
      users += User(
        name = "Fred Smith",
        height = 189,
        hasDog = false,
        city = "Mendocino"
      ),
      users += User(
        name = "Big Guy",
        height = 195,
        hasDog = true,
        city = "New York"
      )
    )

    runAndWait(setupAction)

    val allUsers: Seq[User] = runAndWait(users.result)
    allUsers.foreach(println)

    val distinctQuery: Query[Rep[User], User, Seq] = users.filter(_.city like "M%").distinct

    println("Distinct query:")
    for (i <- 1 to 6) {
      val action = distinctQuery.result
      print(s"SQL to run: ${action.statements}")

      try {
        val distinctResult = runAndWait(distinctQuery.result)
        println(s" Success. Number of users: ${distinctResult.length}")
      } catch {
        case e: Throwable =>
          // to see full stack trace uncomment this:
          // throw e
          println(s"\n   !! FAILED --- Distinct query run failed with exception: ${e.getMessage}, Cause: ${e.getCause.getMessage}")
      }
    }

  } finally {
    println("####### End of test, closing db connection #######\n\n")
    db.close
  }
}

case class User(
    name: String,
    height: Int,
    hasDog: Boolean,
    city: String,
    id: Option[Int] = None)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def height = column[Int]("HEIGHT")
  def hasDog = column[Boolean]("HAS_DOG")
  def city = column[String]("CITY")

  def * = (name, height, hasDog, city, id.?) <> (User.tupled, User.unapply)
}
