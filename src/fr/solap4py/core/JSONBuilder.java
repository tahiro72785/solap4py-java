package fr.solap4py.core;

import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.olap4j.Axis;
import org.olap4j.Cell;
import org.olap4j.CellSet;
import org.olap4j.OlapException;
import org.olap4j.Position;
import org.olap4j.metadata.Member;

final class JSONBuilder {

    private JSONBuilder() {
    }

    
    /**
     * 
     * @param cellSet The CellSet containing the response of a request sent. 
     * @return the JsonArray containing the same information as in cellSet.
     * @throws OlapException
     * @throws JSONException
     */
    
    static JSONArray createJSONResponse(CellSet cellSet) throws OlapException, JSONException {
        JSONArray results = new JSONArray();
        boolean hasRows = false;

        if (cellSet.getAxes().size() > 1) {
            hasRows = true;
        }

        if (hasRows) {
            for (Position axis1 : cellSet.getAxes().get(Axis.ROWS.axisOrdinal()).getPositions()) {
                JSONObject result = new JSONObject();
                for (Position axis0 : cellSet.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositions()) {

                    final Cell cell = cellSet.getCell(axis0, axis1);

                    for (Member member : axis1.getMembers()) {
                        result.put(member.getHierarchy().getUniqueName(), member.getUniqueName());
                    }

                    for (Member member : axis0.getMembers()) {
                        if (cell.getValue() == null) {
                            result.put(member.getUniqueName(), 0);
                        } else {
                            result.put(member.getUniqueName(), cell.getValue());
                        }
                    }
                }
                results.put(result);
            }
        } else {
            JSONObject result = new JSONObject();
            for (Position axis0 : cellSet.getAxes().get(Axis.COLUMNS.axisOrdinal()).getPositions()) {
                final Cell cell = cellSet.getCell(axis0);

                Member member = axis0.getMembers().get(0);
                if (cell.getValue() == null) {
                    result.put(member.getUniqueName(), 0);
                } else {
                    result.put(member.getUniqueName(), cell.getValue());
                }

            }
            results.put(result);

        }
        return results;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Solap4py s = Solap4py.getSolap4Object();

        String query = "{\"queryType\":\"data\",\"data\":{\"from\":\"[Traffic]\",\"onColumns\":[\"[Measures].[Goods Quantity]\",\"[Measures].[Max Quantity]\"],\"onRows\":{},\"where\":{}}}";

        System.out.println(s.process(query));

    }
}
