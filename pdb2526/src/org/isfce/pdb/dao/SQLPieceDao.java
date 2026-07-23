package org.isfce.pdb.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.isfce.pdb.exceptions.InstallationException;
import org.isfce.pdb.model.Piece;
import org.isfce.pdb.model.Plan;
import org.isfce.pdb.model.TypePiece;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SQLPieceDao implements IPieceDao {

    private static String SQL_GET_FROM_ID = """
            SELECT NOM_PIE, DESCRIPTION_PIE, ETAGE_PIE, FKTYPE_PIE, FKINSTALLATION_PIE, FKPLAN_PIE, X_NOM_PIE, Y_NOM_PIE
            FROM TPIECE WHERE NUM_PIE = ?
            """;

    private static String SQL_GET_LISTE_FROM_INST = """
            SELECT NUM_PIE, NOM_PIE, DESCRIPTION_PIE, ETAGE_PIE, FKTYPE_PIE, FKPLAN_PIE, X_NOM_PIE, Y_NOM_PIE
            FROM TPIECE WHERE FKINSTALLATION_PIE=? ORDER BY ETAGE_PIE, FKTYPE_PIE
            """;

    private static String SQL_INSERT = """
            INSERT INTO TPIECE (NOM_PIE, DESCRIPTION_PIE, ETAGE_PIE, FKTYPE_PIE, FKINSTALLATION_PIE, FKPLAN_PIE)
            VALUES (?,?,?,?,?,?)
            """;

    private static String SQL_UPDATE = """
            UPDATE TPIECE SET NOM_PIE = ?, DESCRIPTION_PIE = ?, ETAGE_PIE = ?, FKTYPE_PIE = ?, FKPLAN_PIE = ?, X_NOM_PIE = ?, Y_NOM_PIE = ?
            WHERE NUM_PIE = ?
            """;

    private static String SQL_DELETE = """
            DELETE FROM TPIECE WHERE NUM_PIE=?
            """;

    private DAOFactory factory;
    private Connection connexion;

    public SQLPieceDao(DAOFactory factory) {
        this.factory = factory;
        this.connexion = factory.getConnection();
    }

    @Override
    public Optional<Piece> getFromID(Integer id) {
        Piece obj = null;
        try (PreparedStatement ps = connexion.prepareStatement(SQL_GET_FROM_ID)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
            	// tout capturer depuis rs d'abord 
                String nom = rs.getString("NOM_PIE");
                String desc = rs.getString("DESCRIPTION_PIE");
                BigDecimal etage = rs.getBigDecimal("ETAGE_PIE").setScale(1);
                String typeCode = rs.getString("FKTYPE_PIE");
                int instId = rs.getInt("FKINSTALLATION_PIE");
                Integer fkPlan = (Integer) rs.getObject("FKPLAN_PIE");
                double xNom = rs.getDouble("X_NOM_PIE");	// new
                double yNom = rs.getDouble("Y_NOM_PIE");	// new
                // rs peut fermé maintenant
                
                // résoudre les objets liés
                TypePiece tp = factory.getTypePieceDAO().getFromID(typeCode).get();	// cache est ok
                Plan plan = resolvePlan(fkPlan);	// ouvre un 2e curseur 

                obj = Piece.builder()
                        .id(id)
                        .nom(nom)
                        .description(desc)
                        .etage(etage)
                        .typePiece(tp)
                        .installation(instId)
                        .plan(plan)
                        .xNom(xNom)
                        .yNom(yNom)
                        .build();
                log.debug("Une pièce est chargée: " + obj);
            }
        } catch (SQLException | InstallationException e) {
            log.error("getFromID ERREUR: " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.ofNullable(obj);
    }

    /**
     * Représentation brute d'une ligne TPIECE avant résolution des objets liés.
     * Nécessaire pour fermer le ResultSet avant d'appeler d'autres DAO sur
     * la même connexion (sinon Firebird ferme implicitement le curseur).
     */
    private record RawPiece(int id, String nom, String description, BigDecimal etage,
                              String typeCode, Integer fkPlan, double xNom, double yNom) {
    }

    @Override
    public List<Piece> getListeFromInstallation(Integer installation) {
        List<Piece> liste = new ArrayList<Piece>();
        List<RawPiece> brut = new ArrayList<>();

        // 1ère passe : on lit toutes les lignes brutes, puis on ferme le ResultSet
        try (PreparedStatement ps = connexion.prepareStatement(SQL_GET_LISTE_FROM_INST)) {
            ps.setInt(1, installation);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                brut.add(new RawPiece(
                        rs.getInt("NUM_PIE"),
                        rs.getString("NOM_PIE"),
                        rs.getString("DESCRIPTION_PIE"),
                        rs.getBigDecimal("ETAGE_PIE").setScale(1),
                        rs.getString("FKTYPE_PIE"),
                        (Integer) rs.getObject("FKPLAN_PIE"),
                        rs.getDouble("X_NOM_PIE"),	// new
                        rs.getDouble("Y_NOM_PIE")	// new
                ));
            }
        } catch (SQLException e) {
            log.error("Problème lors du chargement de la liste des Pièces: " + e.getMessage(), e);
            return liste;
        }

        // 2ème passe : résolution des objets liés (TypePiece, Plan), curseur fermé
        for (RawPiece r : brut) {
            try {
                TypePiece tp = factory.getTypePieceDAO().getFromID(r.typeCode()).get();
                Plan plan = resolvePlan(r.fkPlan());
                Piece obj = Piece.builder()
                        .id(r.id())
                        .nom(r.nom())
                        .description(r.description())
                        .etage(r.etage())
                        .typePiece(tp)
                        .installation(installation)
                        .plan(plan)
                        .xNom(r.xNom())		// new
                        .yNom(r.yNom())		// new
                        .build();
                liste.add(obj);
            } catch (InstallationException e) {
                log.error("Problème lors de la résolution de la pièce " + r.id() + ": " + e.getMessage(), e);
            }
        }
        return liste;
    }

    /**
     * Résout l'id de plan (FK nullable) en objet Plan complet
     */
    private Plan resolvePlan(Integer fkPlan) throws InstallationException {
        if (fkPlan == null)
            return null;
        return factory.getPlanDAO().getFromId(fkPlan).orElse(null);
    }

    @Override
    public Piece insert(Piece obj) throws Exception {
        assert obj != null && obj.getId() == null : "L'objet doit exister sans ID";
        try (PreparedStatement ps = connexion.prepareStatement(
                SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, obj.getNom().trim());
            ps.setString(2, obj.getDescription().trim());
            ps.setBigDecimal(3, obj.getEtage());
            ps.setString(4, obj.getTypePiece().getCode());
            ps.setInt(5, obj.getInstallation());
            Integer idPlan = obj.getPlan() != null ? obj.getPlan().getId() : null;
            ps.setObject(6, idPlan, java.sql.Types.INTEGER);
            int nb = ps.executeUpdate();
            if (nb == 1) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    obj.setId(rs.getInt(1));
                    if (!this.connexion.getAutoCommit())
                        this.connexion.commit();
                } else
                    log.error("L'insert n'a pas retourné l'ID auto généré");
            } else
                log.error("L'insert n'a pas fonctionné");
        } catch (SQLException e) {
            log.error("Insertion non validée: " + e);
            if (!this.connexion.getAutoCommit())
                this.connexion.rollback();
            this.factory.dispatchException(e, "[INS] PIECE");
        }
        return obj;
    }

    @Override
    public boolean update(Piece obj) throws Exception {
        boolean ok = false;
        try (PreparedStatement ps = connexion.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, obj.getNom());
            ps.setString(2, obj.getDescription());
            ps.setBigDecimal(3, obj.getEtage());
            ps.setString(4, obj.getTypePiece().getCode());
            // nouvelle modif
            if (obj.getPlan() != null)
            	ps.setInt(5, obj.getPlan().getId());
            else
            	ps.setNull(5, java.sql.Types.INTEGER);
            ps.setDouble(6, obj.getXNom());
            ps.setDouble(7, obj.getYNom());
            ps.setInt(8, obj.getId());	// décalé de 6 à 8
            int nb = ps.executeUpdate();
            if (nb == 1) {
            	if (!this.connexion.getAutoCommit())
            		this.connexion.commit();
            	ok = true;
            }
        } catch (SQLException e) {
            log.error("Mise à jour non validée: " + e);
            if (!this.connexion.getAutoCommit())
                this.connexion.rollback();
            this.factory.dispatchException(e, "[UPD] PIECE");
        }
        return ok;
    }

    @Override
    public boolean delete(Piece obj) throws Exception {
        assert obj != null : "L'objet Pièce ne peut pas être null";
        boolean ok = false;
        try (PreparedStatement ps = connexion.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, obj.getId());
            int nb = ps.executeUpdate();
            if (nb == 1) {
                if (!this.connexion.getAutoCommit())
                	this.connexion.commit();
                ok = true;
            }
        } catch (SQLException e) {
            log.error("Suppression non validée: " + e);
            if (!this.connexion.getAutoCommit())
                this.connexion.rollback();
            this.factory.dispatchException(e, "[DEL] PIECE");
        }
        return ok;
    }

    @Override
    public List<Piece> getListe(String regExpr) {
        return new ArrayList<>();
    }

    @Override
    public int count() {
        return 0;
    }
}