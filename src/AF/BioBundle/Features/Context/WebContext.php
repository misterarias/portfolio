<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 4/05/15
 * Time: 18:51
 */

namespace AF\BioBundle\Features\Context;

class WebContext extends BaseWebContext {

    /**
     * @Then /^I should find a navigation bar with (\d+) items$/
     * @param $nItems
     */
    public function iShouldFindANavigationBarWithItems($nItems)
    {
        $this->assertSession()->elementExists("css", ".navbar-nav");
        $this->assertSession()->elementsCount("css", ".navbar-nav li", $nItems);
    }
}