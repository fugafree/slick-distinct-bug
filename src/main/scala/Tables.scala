import slick.driver.H2Driver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

// A Suppliers table with 3 columns: id, name, city
class Suppliers(tag: Tag)
  extends Table[(Int, String, String)](tag, "SUPPLIERS") {

  // This is the primary key column:
  def id: Rep[Int] = column[Int]("SUP_ID", O.PrimaryKey)
  def name: Rep[String] = column[String]("SUP_NAME")
  def city: Rep[String] = column[String]("CITY")

  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(Int, String, String)] =
    (id, name, city)
}
