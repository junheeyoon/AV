package com.av.ajouuniv.avproject2.data;

public class NetworkExample
{

    private Details details;

    public Details getDetails ()
    {
        return details;
    }

    public void setDetails (Details details)
    {
        this.details = details;
    }

    public class Details
    {

        private String isOk;
        private String message;

        public String getIsOk() {
            return isOk;
        }

        public void setIsOk(String isOk) {
            this.isOk = isOk;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}