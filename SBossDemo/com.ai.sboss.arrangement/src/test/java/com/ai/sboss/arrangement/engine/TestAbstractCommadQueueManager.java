package com.ai.sboss.arrangement.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

import com.ai.sboss.arrangement.JUnit4ClassRunner;
import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 关键测试。测试命令集合管理器的索引工作正确性
 * @author yinwenjie
 */
@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:application-*.xml"})
public class TestAbstractCommadQueueManager extends AbstractCommadQueueManager {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(TestAbstractCommadQueueManager.class); 
	
	@Test
	public void test1() {
		/*
		 * 生成三种不同的命令，然后顺序调用命令
		 * */
		ICommand command1 = new CommandOne();
		ICommand command2 = new CommandTow();
		ICommand command3 = new CommandThree();
		try {
			this.initCommandsQueue(command1,command2,command3);
		} catch (BizException e) {
			TestAbstractCommadQueueManager.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		
		//开始正向执行
		ICommand command = null;
		try {
			while((command = this.nextCommand()) != null) {
				command.execute();
			}
		} catch (BizException e) {
			TestAbstractCommadQueueManager.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void test2() {
		/*
		 * 生成三种不同的命令，然后逆向调用命令
		 * */
		ICommand command1 = new CommandOne();
		ICommand command2 = new CommandTow();
		ICommand command3 = new CommandThree();
		try {
			this.initCommandsQueue(command1,command2,command3);
		} catch (BizException e) {
			TestAbstractCommadQueueManager.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		
		//开始逆向执行
		ICommand command = null;
		try {
			this.peekLastDONOTReturn();
			while((command = this.nextUndoCommand()) != null) {
				command.undo();
			}
		} catch (BizException e) {
			TestAbstractCommadQueueManager.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		
		//换个方式，再来一次
		TestAbstractCommadQueueManager.LOGGER.info("==========换个方式，再来一次============");
		command = null;
		try {
			command = this.peekLast();
			if(command != null) {
				do {
					command.undo();
				} while((command = this.nextUndoCommand()) != null);
			}
		} catch (BizException e) {
			TestAbstractCommadQueueManager.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
	}
	
	@Test
	public void test3() {
		/*
		 * 生成三种不同的命令，前面两个正向执行，到第三个，模拟一场，然后逆向执行命令。
		 * */
		ICommand command1 = new CommandOne();
		ICommand command2 = new CommandTow();
		ICommand command3 = new CommandFour();
		try {
			this.initCommandsQueue(command1,command2,command3);
		} catch (BizException e) {
			TestAbstractCommadQueueManager.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		
		//开始正向执行
		ICommand command = null;
		try {
			command = this.nextCommand();
			command.execute();
			command = this.nextCommand();
			command.execute();
			command = this.nextCommand();
		} catch (BizException e) {
			TestAbstractCommadQueueManager.LOGGER.error(e.getMessage(), e);
			Assert.assertTrue(false);
		}
		
		//模拟第三个异常(CommandFour类，带有异常抛出)
		try {
			command.execute();
		} catch (BizException e) {
			try {
				while((command = this.nextUndoCommand()) != null) {
					command.undo();
				}
			} catch (BizException e1) {
				TestAbstractCommadQueueManager.LOGGER.error(e1.getMessage(), e1);
				Assert.assertTrue(false);
			}
		}
	}
}

/**
 * 第一个命令
 * @author yinwenjie
 */
class CommandOne implements ICommand {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(CommandOne.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#init()
	 */
	@Override
	public void init() throws BizException {
		CommandOne.LOGGER.info("第一个命令初始化");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#execute()
	 */
	@Override
	public void execute() throws BizException {
		CommandOne.LOGGER.info("第一个命令正向执行");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#undo()
	 */
	@Override
	public void undo() throws BizException {
		CommandOne.LOGGER.info("第一个命令逆向执行");
	}
}

/**
 * 第二个命令
 * @author yinwenjie
 */
class CommandTow implements ICommand {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(CommandTow.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#init()
	 */
	@Override
	public void init() throws BizException {
		CommandTow.LOGGER.info("第二个命令初始化");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#execute()
	 */
	@Override
	public void execute() throws BizException {
		CommandTow.LOGGER.info("第二个命令正向执行");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#undo()
	 */
	@Override
	public void undo() throws BizException {
		CommandTow.LOGGER.info("第二个命令逆向执行");
	}
}

/**
 * 第三个命令
 * @author yinwenjie
 */
class CommandThree implements ICommand {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(CommandThree.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#init()
	 */
	@Override
	public void init() throws BizException {
		CommandThree.LOGGER.info("第三个命令初始化");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#execute()
	 */
	@Override
	public void execute() throws BizException {
		CommandThree.LOGGER.info("第三个命令正向执行");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#undo()
	 */
	@Override
	public void undo() throws BizException {
		CommandThree.LOGGER.info("第三个命令逆向执行");
	}
}

/**
 * 第四个命令（带正向执行异常的命令）
 * @author yinwenjie
 */
class CommandFour implements ICommand {
	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(CommandThree.class);
	
	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#init()
	 */
	@Override
	public void init() throws BizException {
		CommandFour.LOGGER.info("第四个命令初始化");
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#execute()
	 */
	@Override
	public void execute() throws BizException {
		CommandFour.LOGGER.info("第四个命令正向执行======正向执行异常");
		throw new BizException("正向执行异常", ResponseCode._501);
	}

	/* (non-Javadoc)
	 * @see com.ai.sboss.arrangement.engine.EngineCommand#undo()
	 */
	@Override
	public void undo() throws BizException {
		CommandFour.LOGGER.info("第四个命令逆向执行");
	}
}