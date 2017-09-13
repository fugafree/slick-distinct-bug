import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._

object SlickDistinctBug extends App {
  val db = Database.forConfig("h2mem1")
  try {

    val users: TableQuery[Users] = TableQuery[Users]

    val setupAction: DBIO[Unit] = DBIO.seq(
      users.schema.create,
      users += User("John Doe", "Groundsville"),
      users += User("Fred Smith", "Mendocino"),
      users += User("Big Guy", "Meadows"),
      users += User("Little Washington", "New York"),
      users += User("Stu", "Meadows")
    )

    val setupFuture: Future[Unit] = db.run(setupAction)
    val f = setupFuture.flatMap { _ =>

      val allUsersFuture: Future[Seq[User]] = db.run(users.result)

      allUsersFuture.map { allSuppliers =>
        allSuppliers.foreach(println)
      }

    }.flatMap { _ =>

      val filterQuery: Query[Rep[String], String, Seq] =
        users.filter(_.city like "M%").map(_.city).distinct

      println("Generated SQL for filter query:\n" + filterQuery.result.statements)

      println("Distinct cities starts with 'M':")
      db.run(filterQuery.result.map(println))

    }

    Await.result(f, Duration.Inf)

  } finally db.close
}

case class User(
    name: String,
    city: String,
    id: Option[Int] = None)

class Users(tag: Tag) extends Table[User](tag, "USERS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def name = column[String]("NAME")
  def city = column[String]("CITY")

  def * = (name, city, id.?) <> (User.tupled, User.unapply)
}
