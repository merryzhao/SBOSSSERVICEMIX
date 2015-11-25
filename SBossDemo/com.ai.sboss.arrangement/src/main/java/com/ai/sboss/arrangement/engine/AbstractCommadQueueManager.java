package com.ai.sboss.arrangement.engine;

import java.util.Arrays;
import java.util.LinkedList;

import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 命令集合管理类。这个类中封装了一个java.util.LinkedList&lt;EngineCommand&gt;<br>
 * 通过队列提供了Command的执行顺序。这样封装以后。命令管控就统一起来了， 其子类只需要告诉父类：“我的命令装载顺序是什么样的”<br>
 * 注意，根据这个命令管理器的使用场景，不会出现多个线程同时依据一个命令管理器实例中的命令进行执行的可能，所以不需要使用ConcurrentLinkedQueue队列。
 * 但是，可能会出现在命令执行过程中，其他线程使用add和push方法 进行新命令插入的情况（虽然不多见），所以需要在相应方法上加同步锁（因为LinkedList不是线程安全的）。
 * <p>
 * <b>注意，由于AbstractCommadQueueManager不是线程安全的，所以不要使用同一个AbstractCommadQueueManager的子类对象在多个线程中同时操作</b><br>
 * （实际上这样的情况不会出现）
 * @author yinwenjie
 *
 */
public abstract class AbstractCommadQueueManager {
	
	private LinkedList<ICommand> engineCommands = new LinkedList<ICommand>(); 
	
	/**
	 * 当前正在执行的命令的索引号，初始值为-1;
	 */
	private Integer index = -1;
	
	/**
	 * 初始化命令队列。命令数组中的第一个元素将作为队列头部的第一个元素，以此类推
	 * @param commands 要完成一个业务，所需要的所有命令的数组集合，命令的顺序执行将从第一个元素开始。该参数必须传入至少一个元素
	 * @throws BizException 如果发现commands中没有任何元素，则会抛出这个异常
	 */
	protected void initCommandsQueue(ICommand... commands) throws BizException {
		if(commands == null || commands.length == 0) {
			throw new BizException("初始化命令控制器，至少需要设置一个命令", ResponseCode._403);
		}
		
		this.engineCommands.clear();
		this.index = -1;
		this.engineCommands.addAll(Arrays.asList(commands));
		//然后执行初始化
		for (ICommand engineCommand : commands) {
			engineCommand.init();
		}
	}
	
	/**
	 * 在命令管理器队列的尾部新增一个新的命令
	 * @param command 
	 * @throws BizException 注意，新增的命令不能为空。为了保证命令执行的可行性，也不能将已经存在于队列的命令对象再次添加到队列
	 */
	protected synchronized void addCopmmand(ICommand command) throws BizException {
		if(command == null) {
			throw new BizException("新增的命令不能为空,请检查", ResponseCode._401);
		}
		if(this.engineCommands.contains(command)) {
			throw new BizException("同一个命令不能重复进行添加,请检查", ResponseCode._401);
		}
		
		this.engineCommands.add(command);
	}
	
	/**
	 * 在命令管理器队列的头部新增一个新的命令
	 * @param command
	 * @throws BizException 注意，新增的命令不能为空。为了保证命令执行的可行性，也不能将已经存在于队列的命令对象再次添加到队列
	 */
	protected synchronized void pushCopmmand(ICommand command) throws BizException {
		if(command == null) {
			throw new BizException("新增的命令不能为空,请检查", ResponseCode._401);
		}
		if(this.engineCommands.contains(command)) {
			throw new BizException("同一个命令不能重复进行添加,请检查", ResponseCode._401);
		}
		
		this.engineCommands.push(command);
	}
	
	/**
	 * 获取在命令管理器正向执行时，需要执行的下一个命令。<br>
	 * 实际上就是命令管理器队列中下一个命令对象
	 * @return 如果队列中最后一个元素已经被获取了（index + 1 == size）,再次执行这个方法，将会返回null
	 * @throws BizException
	 */
	protected synchronized ICommand nextCommand() throws BizException {
		if(this.index + 1 >= this.engineCommands.size()) {
			return null;
		}
		
		return this.engineCommands.get(++this.index);
	}
	
	/**
	 * 获取在命令管理器逆向执行时，需要执行的下一个命令。<br>
	 * 实际上就是命令管理器队列中上一个命令对象
	 * @return 如果队列中第一元素已经被获取了(index == 0时)，再次执行这个方法，将会返回null
	 * @throws BizException
	 */
	protected synchronized ICommand nextUndoCommand() throws BizException {
		if(this.index < 0 || this.engineCommands.isEmpty()) {
			return null;
		}
		
		return this.engineCommands.get(this.index--);
	}
	
	/**
	 * 执行该方法，无论当前索引位置在命令管理器的哪个位置，都将被指向队列的最底部的位置，并且同时返回最底部的命令对象。
	 * @return 如果队列中没有任何数据，将返回null
	 * @throws BizException
	 */
	protected synchronized ICommand peekLast() throws BizException {
		this.index = 0;
		if(this.engineCommands.isEmpty()) {
			return null;
		}
		
		this.index = this.engineCommands.size() - 1;
		return this.engineCommands.get(this.index--);
	}
	
	/**
	 * 执行该方法，无论当前索引位置在命令管理器的哪个位置，都将被指向队列的最底部的位置。
	 * @throws BizException
	 */
	protected synchronized void peekLastDONOTReturn() throws BizException {
		this.index = 0;
		if(this.engineCommands.isEmpty()) {
			return;
		}
		
		this.index = this.engineCommands.size() - 1;
	}
}