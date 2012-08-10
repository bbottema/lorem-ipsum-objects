/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.opensource.org/licenses/cddl1.php
 * or http://www.opensource.org/licenses/cddl1.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.opensource.org/licenses/cddl1.php.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is dummyCreator. The Initial Developer of the Original
 * Software is Alexander Muthmann <amuthmann@dev-eth0.de>.
 */
package org.dummycreator.helperutils;

public class InheritedPrimitiveClass extends PrimitiveClass {

    private String secondString;

    /**
     * Get the value of secondString
     * 
     * @return the value of secondString
     */
    public String getSecondString() {
	return secondString;
    }

    /**
     * Set the value of secondString
     * 
     * @param secondString new value of secondString
     */
    public void setSecondString(String secondString) {
	this.secondString = secondString;
    }

}
