package an.xacml.context;

import an.xacml.DefaultXACMLElement;

public class Response extends DefaultXACMLElement {
    private Result[] results;
    private int hashCode;

    public Response(Result[] evalResults) {
        this.results = evalResults;

        hashCode = 11;
        for (Result result : results) {
            hashCode = hashCode * 13 + result.hashCode();
        }
    }

    public Result[] getResults() {
        return results;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o != null && o.getClass() == this.getClass()) {
            if (results != null && ((Response)o).results != null && results.length == ((Response)o).results.length) {
                for (int i = 0; i < results.length; i ++) {
                    if (!results[i].equals(((Response)o).results[i])) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}