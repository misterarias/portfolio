<?php
/**
 * Created by PhpStorm.
 * User: juanito
 * Date: 07/05/15
 * Time: 14:31
 */

namespace AF\Controller;

use AF\Controller\Modules\Module;
use Symfony\Bundle\FrameworkBundle\Controller\Controller;
use Symfony\Component\HttpFoundation\Response;

/**
 * Class AFController
 *
 * This class is not meant to be called directly, but subclassed.
 *
 * @package AF\Controller
 */
abstract class AFController extends Controller {
	/** @var Module */
	protected $currentModule = null;

	/**
	 * Array of currently enabled modules
	 * @return array[Modules]
	 */
	abstract function getAllowedModules();

	/**
	 * @param String $moduleName
	 */
	protected function setCurrentModule($moduleName) {
		/** @var Module $module */
		foreach ($this->getAllowedModules() as $module) {
			if ($module->getName() === $moduleName) {
				$this->currentModule = $module;

				return;
			}
		}
		throw new \RuntimeException("Trying to access unconfigured module: " . $module->getName());
	}

	protected function setNextModule($moduleName) {
		/** @var Module $module */
		foreach ($this->getAllowedModules() as $module) {
			if ($module->getName() === $moduleName) {
				$this->currentModule->setNextModule($module);
				return;
			}
		}
	}

	protected function setPreviousModule($moduleName) {
		/** @var Module $module */
		foreach ($this->getAllowedModules() as $module) {
			if ($module->getName() === $moduleName) {
				$this->currentModule->setPreviousModule($module);
				return;
			}
		}
	}

	public function render($view, array $parameters = array(), Response $response = null) {
		// This is used in the navbar
		$parameters["activeModule"] = $this->currentModule;
		$parameters["availableModules"] = $this->getAllowedModules();

		return parent::render($view, $parameters, $response);
	}


}