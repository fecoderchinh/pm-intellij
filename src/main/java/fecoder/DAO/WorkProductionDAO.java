package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.PackagingOwnerString;
import fecoder.models.WorkProduction;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkProductionDAO {

    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private WorkProduction createData(ResultSet resultSet) {
        WorkProduction data = new WorkProduction();
        try {
            data.setId(resultSet.getInt("id"));
            data.setWorkOrderName(resultSet.getString("workOrderName"));
            data.setProductName(resultSet.getString("productName"));
            data.setPackagingName(resultSet.getString("packagingName"));
            data.setPackagingSpecification(resultSet.getString("packagingSpecification"));
            data.setPackagingDimension(resultSet.getString("packagingDimension"));
            data.setPackagingSuplier(resultSet.getString("packagingSuplier"));
            data.setPackagingCode(resultSet.getString("packagingCode"));
            data.setUnit(resultSet.getString("unit"));
            data.setPrintStatus(resultSet.getString("printStatus"));
            data.setPackQuantity(resultSet.getInt("packQuantity"));
            data.setWorkOrderQuantity(resultSet.getFloat("workOrderQuantity"));
            data.setStock(resultSet.getFloat("Stock"));
            data.setActualQuantity(resultSet.getFloat("actualQuantity"));
            data.setResidualQuantity(resultSet.getFloat("residualQuantity"));
            data.setTotalResidualQuantity(resultSet.getFloat("totalResidualQuantity"));
            data.setNoteProduct(resultSet.getString("noteProduct"));
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    /**
     * Getting all records of table
     *
     * @return list
     * */
    public List<WorkProduction> getList() {
        List<WorkProduction> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select " +
                    "wopp.id as id, " +
                    "wop.ordinal_num as ordinalNumbers, " +
                    "wo.name as workOrderName, " +
                    "p2.name as productName, " +
                    "p.name as packagingName, " +
                    "p.specifications as packagingSpecification, " +
                    "p.dimension as packagingDimension, " +
                    "p.suplier as packagingSuplier, " +
                    "p.code as packagingCode, " +
                    "t.unit as unit, " +
                    "p.stamped as printStatus, " +
                    "pps.pack_qty as packQuantity, " +
                    "(pps.pack_qty * wop.qty) as workOrderQuantity, " +
                    "wopp.stock as Stock, " +
                    "wopp.actual_qty as actualQuantity, " +
                    "wopp.residual_qty as residualQuantity, " +
                    "(wopp.actual_qty - wopp.residual_qty - wopp.stock - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                    "wop.note as noteProduct " +
                    "from " +
                    "work_order wo, " +
                    "work_order_product wop, " +
                    "packaging_product_size pps, " +
                    "packaging p, " +
                    "products p2, " +
                    "types t, " +
                    "work_order_product_packaging wopp " +
                    "where " +
                    "wo.id = wop.work_order_id " +
                    "and wop.product_id = p2.id " +
                    "and pps.product_id = p2.id " +
                    "and pps.packaging_id = p.id " +
                    "and p.`type` = t.id " +
                    "and wopp.work_order_id = wo.id " +
                    "and wopp.product_id = p2.id " +
                    "and wopp.packaging_id = p.id " +
                    "order by wop.ordinal_num";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkProduction data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }

    /**
     * Getting all records of table
     *
     * @param workOrderID the id of work order
     *
     * @return list
     * */
    public List<WorkProduction> getListByID(int workOrderID) {
        List<WorkProduction> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select " +
                    "wopp.id as id, " +
                    "wop.ordinal_num as ordinalNumbers, " +
                    "wo.name as workOrderName, " +
                    "p2.name as productName, " +
                    "p.name as packagingName, " +
                    "p.specifications as packagingSpecification, " +
                    "p.dimension as packagingDimension, " +
                    "p.suplier as packagingSuplier, " +
                    "p.code as packagingCode, " +
                    "t.unit as unit, " +
                    "p.stamped as printStatus, " +
                    "pps.pack_qty as packQuantity, " +
                    "(pps.pack_qty * wop.qty) as workOrderQuantity, " +
                    "wopp.stock as Stock, " +
                    "wopp.actual_qty as actualQuantity, " +
                    "wopp.residual_qty as residualQuantity, " +
                    "(wopp.actual_qty - wopp.residual_qty - wopp.stock - (pps.pack_qty * wop.qty)) as totalResidualQuantity, " +
                    "wop.note as noteProduct " +
                    "from " +
                    "work_order wo, " +
                    "work_order_product wop, " +
                    "packaging_product_size pps, " +
                    "packaging p, " +
                    "products p2, " +
                    "types t, " +
                    "work_order_product_packaging wopp " +
                    "where " +
                    "wo.id = wop.work_order_id " +
                    "and wop.product_id = p2.id " +
                    "and pps.product_id = p2.id " +
                    "and pps.packaging_id = p.id " +
                    "and p.`type` = t.id " +
                    "and wopp.work_order_id = wo.id " +
                    "and wopp.product_id = p2.id " +
                    "and wopp.packaging_id = p.id " +
                    "and wo.id = "+workOrderID +
                    " order by wop.ordinal_num";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                WorkProduction data = createData(resultSet);
                list.add(data);
            }
            resultSet.close();
            conn.close();
        } catch (ClassNotFoundException | SQLException ex) {
            assert ex instanceof SQLException;
            jdbcDAO.printSQLException((SQLException) ex);
        }
        return list;
    }
}
