/*// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: CommandsProtocol.proto
package io.netty.commands;
public final class CommandsProtocol_1 {
  private CommandsProtocol_1() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public interface CommandOrBuilder
      extends com.google.protobuf.MessageOrBuilder {

    // required uint32 commandId = 1;
    *//**
     * <code>required uint32 commandId = 1;</code>
     *//*
    boolean hasCommandId();
    *//**
     * <code>required uint32 commandId = 1;</code>
     *//*
    int getCommandId();

    // required string commandString = 2;
    *//**
     * <code>required string commandString = 2;</code>
     *//*
    boolean hasCommandString();
    *//**
     * <code>required string commandString = 2;</code>
     *//*
    java.lang.String getCommandString();
    *//**
     * <code>required string commandString = 2;</code>
     *//*
    com.google.protobuf.ByteString
        getCommandStringBytes();
  }
  *//**
   * Protobuf type {@code Command}
   *//*
  public static final class Command extends
      com.google.protobuf.GeneratedMessage
      implements CommandOrBuilder {
    // Use Command.newBuilder() to construct.
    private Command(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
      super(builder);
      this.unknownFields = builder.getUnknownFields();
    }
    private Command(boolean noInit) { this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance(); }

    private static final Command defaultInstance;
    public static Command getDefaultInstance() {
      return defaultInstance;
    }

    public Command getDefaultInstanceForType() {
      return defaultInstance;
    }

    private final com.google.protobuf.UnknownFieldSet unknownFields;
    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
      return this.unknownFields;
    }
    private Command(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      initFields();
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
            case 8: {
              bitField0_ |= 0x00000001;
              commandId_ = input.readUInt32();
              break;
            }
            case 18: {
              bitField0_ |= 0x00000002;
              commandString_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e.getMessage()).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return CommandsProtocol.internal_static_Command_descriptor;
    }

    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return CommandsProtocol.internal_static_Command_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              CommandsProtocol.Command.class, CommandsProtocol.Command.Builder.class);
    }

    public static com.google.protobuf.Parser<Command> PARSER =
        new com.google.protobuf.AbstractParser<Command>() {
      public Command parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Command(input, extensionRegistry);
      }
    };

    @java.lang.Override
    public com.google.protobuf.Parser<Command> getParserForType() {
      return PARSER;
    }

    private int bitField0_;
    // required uint32 commandId = 1;
    public static final int COMMANDID_FIELD_NUMBER = 1;
    private int commandId_;
    *//**
     * <code>required uint32 commandId = 1;</code>
     *//*
    public boolean hasCommandId() {
      return ((bitField0_ & 0x00000001) == 0x00000001);
    }
    *//**
     * <code>required uint32 commandId = 1;</code>
     *//*
    public int getCommandId() {
      return commandId_;
    }

    // required string commandString = 2;
    public static final int COMMANDSTRING_FIELD_NUMBER = 2;
    private java.lang.Object commandString_;
    *//**
     * <code>required string commandString = 2;</code>
     *//*
    public boolean hasCommandString() {
      return ((bitField0_ & 0x00000002) == 0x00000002);
    }
    *//**
     * <code>required string commandString = 2;</code>
     *//*
    public java.lang.String getCommandString() {
      java.lang.Object ref = commandString_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        if (bs.isValidUtf8()) {
          commandString_ = s;
        }
        return s;
      }
    }
    *//**
     * <code>required string commandString = 2;</code>
     *//*
    public com.google.protobuf.ByteString
        getCommandStringBytes() {
      java.lang.Object ref = commandString_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        commandString_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private void initFields() {
      commandId_ = 0;
      commandString_ = "";
    }
    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized != -1) return isInitialized == 1;

      if (!hasCommandId()) {
        memoizedIsInitialized = 0;
        return false;
      }
      if (!hasCommandString()) {
        memoizedIsInitialized = 0;
        return false;
      }
      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        output.writeUInt32(1, commandId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        output.writeBytes(2, getCommandStringBytes());
      }
      getUnknownFields().writeTo(output);
    }

    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) == 0x00000001)) {
        size += com.google.protobuf.CodedOutputStream
          .computeUInt32Size(1, commandId_);
      }
      if (((bitField0_ & 0x00000002) == 0x00000002)) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(2, getCommandStringBytes());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    protected java.lang.Object writeReplace()
        throws java.io.ObjectStreamException {
      return super.writeReplace();
    }

    public static CommandsProtocol.Command parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CommandsProtocol.Command parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CommandsProtocol.Command parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static CommandsProtocol.Command parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static CommandsProtocol.Command parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static CommandsProtocol.Command parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }
    public static CommandsProtocol.Command parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input);
    }
    public static CommandsProtocol.Command parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseDelimitedFrom(input, extensionRegistry);
    }
    public static CommandsProtocol.Command parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return PARSER.parseFrom(input);
    }
    public static CommandsProtocol.Command parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return PARSER.parseFrom(input, extensionRegistry);
    }

    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(CommandsProtocol.Command prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessage.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    *//**
     * Protobuf type {@code Command}
     *//*
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder>
       implements CommandsProtocol.CommandOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return CommandsProtocol.internal_static_Command_descriptor;
      }

      protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return CommandsProtocol.internal_static_Command_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                CommandsProtocol.Command.class, CommandsProtocol.Command.Builder.class);
      }

      // Construct using CommandsProtocol.Command.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessage.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
        }
      }
      private static Builder create() {
        return new Builder();
      }

      public Builder clear() {
        super.clear();
        commandId_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        commandString_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      public Builder clone() {
        return create().mergeFrom(buildPartial());
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return CommandsProtocol.internal_static_Command_descriptor;
      }

      public CommandsProtocol.Command getDefaultInstanceForType() {
        return CommandsProtocol.Command.getDefaultInstance();
      }

      public CommandsProtocol.Command build() {
        CommandsProtocol.Command result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public CommandsProtocol.Command buildPartial() {
        CommandsProtocol.Command result = new CommandsProtocol.Command(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
          to_bitField0_ |= 0x00000001;
        }
        result.commandId_ = commandId_;
        if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
          to_bitField0_ |= 0x00000002;
        }
        result.commandString_ = commandString_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof CommandsProtocol.Command) {
          return mergeFrom((CommandsProtocol.Command)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(CommandsProtocol.Command other) {
        if (other == CommandsProtocol.Command.getDefaultInstance()) return this;
        if (other.hasCommandId()) {
          setCommandId(other.getCommandId());
        }
        if (other.hasCommandString()) {
          bitField0_ |= 0x00000002;
          commandString_ = other.commandString_;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }

      public final boolean isInitialized() {
        if (!hasCommandId()) {
          
          return false;
        }
        if (!hasCommandString()) {
          
          return false;
        }
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        CommandsProtocol.Command parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (CommandsProtocol.Command) e.getUnfinishedMessage();
          throw e;
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      // required uint32 commandId = 1;
      private int commandId_ ;
      *//**
       * <code>required uint32 commandId = 1;</code>
       *//*
      public boolean hasCommandId() {
        return ((bitField0_ & 0x00000001) == 0x00000001);
      }
      *//**
       * <code>required uint32 commandId = 1;</code>
       *//*
      public int getCommandId() {
        return commandId_;
      }
      *//**
       * <code>required uint32 commandId = 1;</code>
       *//*
      public Builder setCommandId(int value) {
        bitField0_ |= 0x00000001;
        commandId_ = value;
        onChanged();
        return this;
      }
      *//**
       * <code>required uint32 commandId = 1;</code>
       *//*
      public Builder clearCommandId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        commandId_ = 0;
        onChanged();
        return this;
      }

      // required string commandString = 2;
      private java.lang.Object commandString_ = "";
      *//**
       * <code>required string commandString = 2;</code>
       *//*
      public boolean hasCommandString() {
        return ((bitField0_ & 0x00000002) == 0x00000002);
      }
      *//**
       * <code>required string commandString = 2;</code>
       *//*
      public java.lang.String getCommandString() {
        java.lang.Object ref = commandString_;
        if (!(ref instanceof java.lang.String)) {
          java.lang.String s = ((com.google.protobuf.ByteString) ref)
              .toStringUtf8();
          commandString_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      *//**
       * <code>required string commandString = 2;</code>
       *//*
      public com.google.protobuf.ByteString
          getCommandStringBytes() {
        java.lang.Object ref = commandString_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          commandString_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      *//**
       * <code>required string commandString = 2;</code>
       *//*
      public Builder setCommandString(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        commandString_ = value;
        onChanged();
        return this;
      }
      *//**
       * <code>required string commandString = 2;</code>
       *//*
      public Builder clearCommandString() {
        bitField0_ = (bitField0_ & ~0x00000002);
        commandString_ = getDefaultInstance().getCommandString();
        onChanged();
        return this;
      }
      *//**
       * <code>required string commandString = 2;</code>
       *//*
      public Builder setCommandStringBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        commandString_ = value;
        onChanged();
        return this;
      }

      // @@protoc_insertion_point(builder_scope:Command)
    }

    static {
      defaultInstance = new Command(true);
      defaultInstance.initFields();
    }

    // @@protoc_insertion_point(class_scope:Command)
  }

  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_Command_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_Command_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\026CommandsProtocol.proto\"3\n\007Command\022\021\n\tc" +
      "ommandId\030\001 \002(\r\022\025\n\rcommandString\030\002 \002(\tB\002H" +
      "\001"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_Command_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_Command_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_Command_descriptor,
              new java.lang.String[] { "CommandId", "CommandString", });
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }

  // @@protoc_insertion_point(outer_class_scope)
}
*/