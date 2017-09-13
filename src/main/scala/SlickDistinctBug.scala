import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import slick.driver.H2Driver.api._

// The main application
object SlickDistinctBug extends App {
  val db = Database.forConfig("h2mem1")
  try {

    // The query interface for the Suppliers table
    val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]

    val setupAction: DBIO[Unit] = DBIO.seq(
      // Create the schema by combining the DDLs for the Suppliers
      // tables using the query interfaces
      suppliers.schema.create,

      // Insert some suppliers
      suppliers += (101, "Acme, Inc.", "Groundsville"),
      suppliers += ( 49, "Superior Coffee", "Mendocino"),
      suppliers += (150, "The High Ground", "Meadows"),
      suppliers += ( 21, "The Hole", "New York"),
      suppliers += ( 81, "Great Sup", "Meadows")
    )

    val setupFuture: Future[Unit] = db.run(setupAction)
    val f = setupFuture.flatMap { _ =>

      val allSuppliersAction: DBIO[Seq[(Int, String, String)]] =
        suppliers.result

      val allSuppliersFuture: Future[Seq[(Int, String, String)]] =
        db.run(allSuppliersAction)

      allSuppliersFuture.map { allSuppliers =>
        allSuppliers.foreach(println)
      }

    }.flatMap { _ =>

      /* Filtering / Where */

      // Construct a query where the price of Coffees is > 9.0
      val filterQuery: Query[Rep[String], String, Seq] =
        suppliers.filter(_.city like "M%").map(_.city).distinct

      // Print the SQL for the filter query
      println("Generated SQL for filter query:\n" + filterQuery.result.statements)

      println("Distinct cities starts with 'M':")
      // Execute the query and print the Seq of results
      db.run(filterQuery.result.map(println))

    }

    Await.result(f, Duration.Inf)

  } finally db.close
}
