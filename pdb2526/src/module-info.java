module pdb2526 {
	requires org.slf4j;
	requires org.firebirdsql.jaybird;
	requires java.sql;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires java.base;
	requires lombok;
	
	opens org.isfce.pdb.controller to javafx.fxml, javafx.graphics;
	opens org.isfce.pdb.view.piece to javafx.fxml,  javafx.graphics;
	opens org.isfce.pdb.view.plan to javafx.fxml, javafx.graphics; // new


	exports org.isfce.pdb;
	exports org.isfce.pdb.controller;
	exports org.isfce.pdb.view.piece to javafx.fxml, javafx.graphics;
	exports org.isfce.pdb.view.plan to javafx.fxml, javafx.graphics;
}