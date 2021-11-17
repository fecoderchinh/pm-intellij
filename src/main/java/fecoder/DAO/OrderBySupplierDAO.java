package fecoder.DAO;

import fecoder.connection.ConnectionUtils;
import fecoder.models.Order;
import fecoder.models.OrderBySupllier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrderBySupplierDAO {
    /**
     * Representing a database
     *
     * @param resultSet - A table of data representing a database result set
     * @return data
     * */
    private OrderBySupllier createData(ResultSet resultSet) {
        OrderBySupllier data = new OrderBySupllier();
        try {
            data.setWoName(resultSet.getString("woName"));
            data.setpName(resultSet.getString("pName"));
            data.setpSpecs(resultSet.getString("pSpecs"));
            data.setpDimension(resultSet.getString("pDimension"));
            data.setpUnit(resultSet.getString("pUnit"));
            data.setpDesireQuantity(resultSet.getFloat("pDesireQuantity"));
            data.setpTotal(resultSet.getFloat("pTotal"));
            data.setpStock(resultSet.getFloat("pStock"));
            data.setpResidualQuantity(resultSet.getFloat("pResidualQuantity"));
            data.setsCode(resultSet.getString("sCode"));
            data.setpCode(resultSet.getString("pCode"));
            data.setsName(resultSet.getString("sName"));
            data.setsAddress(resultSet.getString("sAddress"));
            data.setsDeputy(resultSet.getString("sDeputy"));
            data.setsPhone(resultSet.getString("sPhone"));
            data.setsFax(resultSet.getString("sFax"));
        } catch (SQLException ex) {
            jdbcDAO.printSQLException(ex);
        }
        return data;
    }

    /**
     * Getting all records of table
     *
     * @param idList list of work_order_product_packaging.work_order_id
     *
     * @return list
     * */
    public List<OrderBySupllier> getList(String idList) {
        List<OrderBySupllier> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select" +
                    " wo.id as woID," +
                    " group_concat(distinct wo.name separator \"+\") as woName, " +
                    " p.name as pName," +
                    " p.specifications as pSpecs," +
                    " p.dimension as pDimension," +
                    " t.unit as pUnit," +
                    " sum(wopp.work_order_qty) as pDesireQuantity," +
                    " sum(wopp.actual_qty) as pTotal," +
                    " sum(wopp.stock) as pStock," +
                    " sum(wopp.residual_qty) as pResidualQuantity," +
                    " s.code as sCode," +
                    " p.code as pCode," +
                    " s.address as sAddress," +
                    " s.deputy as sDeputy," +
                    " s.name as sName," +
                    " s.phone as sPhone," +
                    " s.fax as sFax " +
                    "from " +
                    " work_order_product_packaging wopp," +
                    " work_order wo," +
                    " packaging p," +
                    " types t ," +
                    " supliers s " +
                    "where " +
                    " wopp.work_order_id = wo.id" +
                    " and wopp.packaging_id = p.id" +
                    " and p.`type` = t.id" +
                    " and p.suplier = s.id" +
                    " and wopp.actual_qty > 0 " +
                    "and wopp.work_order_id in (" + idList + ") "+
                    "group by " +
                    " wopp.packaging_id";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                OrderBySupllier data = createData(resultSet);
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
     * @param idList list of work_order_product_packaging.work_order_id
     * @param code suppliers.code
     *
     * @return list
     * */
    public List<OrderBySupllier> getListBySupplierCode(String idList, String code) {
        List<OrderBySupllier> list = new ArrayList<>();
        try {
            Connection conn = ConnectionUtils.getMyConnection();
            Statement statement = conn.createStatement();
            String selectAll =  "select" +
                    " wo.id as woID," +
                    " group_concat(distinct wo.name separator \"+\") as woName, " +
                    " p.name as pName," +
                    " p.specifications as pSpecs," +
                    " p.dimension as pDimension," +
                    " t.unit as pUnit," +
                    " sum(wopp.work_order_qty) as pDesireQuantity," +
                    " sum(wopp.actual_qty) as pTotal," +
                    " sum(wopp.stock) as pStock," +
                    " sum(wopp.residual_qty) as pResidualQuantity," +
                    " s.code as sCode," +
                    " p.code as pCode," +
                    " s.address as sAddress," +
                    " s.deputy as sDeputy," +
                    " s.name as sName," +
                    " s.phone as sPhone," +
                    " s.fax as sFax " +
                    "from " +
                    " work_order_product_packaging wopp," +
                    " work_order wo," +
                    " packaging p," +
                    " types t ," +
                    " supliers s " +
                    "where " +
                    " wopp.work_order_id = wo.id" +
                    " and wopp.packaging_id = p.id" +
                    " and p.`type` = t.id" +
                    " and p.suplier = s.id" +
                    " and wopp.actual_qty > 0 " +
                    "and wopp.work_order_id in (" + idList + ") "+
                    " and s.code = '" + code + "' " +
                    "group by " +
                    " wopp.packaging_id";
            ResultSet resultSet = statement.executeQuery(selectAll);
            while(resultSet.next()) {
                OrderBySupllier data = createData(resultSet);
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
