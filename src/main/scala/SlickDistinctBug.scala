import slick.driver.H2Driver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object SlickDistinctBug extends App {
  val db = Database.forConfig("h2mem1")
  try {

    val users: TableQuery[Users] = TableQuery[Users]

    val setupAction: DBIO[Unit] = DBIO.seq(
      users.schema.create,
      users += User("John Doe", 167, true, "Meadows"),
      users += User("John Doe", 167, true, "Meadows"),
      users += User("Fred Smith", 189, false, "Mendocino"),
      users += User("Big Guy", 195, true, "Meadows")
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
