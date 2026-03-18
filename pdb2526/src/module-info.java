module pdb2526 {
	requires org.slf4j;
	requires org.firebirdsql.jaybird;
	requires java.sql;
	requires javafx.controls;
	requires javafx.fxml;
	requires transitive javafx.graphics;
	requires java.base;
	requires lombok;
	
	// opens org.isfce.pdb.view to javafx.fxml;
	//opens org.isfce.pdb to org.junit.platform.commons;

	exports org.isfce.pdb;
}