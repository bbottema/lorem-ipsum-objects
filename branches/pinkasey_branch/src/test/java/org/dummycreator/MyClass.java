package org.dummycreator;

/**
 * Created by Eyal on 10/25/13.
 * class to test handling of:
 * <li>Properties</li>
 * <li>private fields</li>
 * <li>public fields</li>
 */
public class MyClass {
    public Integer publicInt;
    private Integer privateInt;
    private Integer memberInt;

    public Integer getMemberInt() {
        return memberInt;
    }

    public Integer getPublicInt() {
        return publicInt;
    }

    public Integer getPrivateInt() {
        return privateInt;
    }

    public void setMemberInt(Integer memberInt) {
        this.memberInt = memberInt;
    }

    @Override
    public String toString() {
        return "MyClass{" +
                "publicInt=" + publicInt +
                ", privateInt=" + privateInt +
                ", memberInt=" + memberInt +
                '}';
    }
}
